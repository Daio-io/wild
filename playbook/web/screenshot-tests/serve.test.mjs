import test from "node:test";
import assert from "node:assert/strict";
import { mkdtemp, rm, writeFile } from "node:fs/promises";
import { spawn } from "node:child_process";
import { request } from "node:http";
import { tmpdir } from "node:os";
import { join } from "node:path";
import { fileURLToPath } from "node:url";

import { resolveRequestPath, startStaticServer } from "./serve.mjs";

const servers = [];

test.afterEach(async () => {
  await Promise.all(servers.splice(0).map((server) => new Promise((resolve) => server.close(resolve))));
});

test("missing root rejects before listening", async () => {
  await assert.rejects(
    startStaticServer("/definitely/missing/screenshot-distribution", 0),
    /does not exist/,
  );
});

test("rejects literal and percent-encoded traversal", async () => {
  const { root, server } = await createServerFixture({ "index.html": "home" });
  const { port } = server.address();

  assert.throws(() => resolveRequestPath(root, "/../package.json"), { code: "TRAVERSAL" });
  const status = await new Promise((resolve, reject) => {
    const request = requestForTraversal(port, resolve, reject);
    request.end();
  });
  assert.equal(status, 403);

  await rm(root, { recursive: true, force: true });
});

test("falls back missing routes to index.html", async () => {
  const { root, server } = await createServerFixture({ "index.html": "home" });
  const { port } = server.address();

  const response = await fetch(`http://127.0.0.1:${port}/missing-route`);
  assert.equal(response.status, 200);
  assert.equal(response.headers.get("content-type"), "text/html");
  assert.equal(await response.text(), "home");

  await rm(root, { recursive: true, force: true });
});

test("serves JavaScript and Wasm with explicit MIME types", async () => {
  const { root, server } = await createServerFixture({
    "app.js": "console.log('app')",
    "app.wasm": "wasm",
  });
  const { port } = server.address();

  const jsResponse = await fetch(`http://127.0.0.1:${port}/app.js`);
  const wasmResponse = await fetch(`http://127.0.0.1:${port}/app.wasm`);
  assert.equal(jsResponse.headers.get("content-type"), "application/javascript");
  assert.equal(wasmResponse.headers.get("content-type"), "application/wasm");

  await rm(root, { recursive: true, force: true });
});

test("rejects startup when the port is occupied", async () => {
  const { root, server: first } = await createServerFixture({ "index.html": "home" });
  const { port } = first.address();

  await assert.rejects(startStaticServer(root, port), { code: "EADDRINUSE" });

  const result = await runCli(root, port);
  assert.notEqual(result.status, 0);
  assert.match(result.stderr, /EADDRINUSE/);

  await rm(root, { recursive: true, force: true });
});

function requestForTraversal(port, resolve, reject) {
  return request({ host: "127.0.0.1", port, path: "/%2e%2e/package.json" }, (response) => {
    response.resume();
    response.once("end", () => resolve(response.statusCode));
  }).on("error", reject);
}

async function createServerFixture(files) {
  const root = await mkdtemp(join(tmpdir(), "screenshot-server-"));
  await Promise.all(Object.entries(files).map(([name, contents]) => writeFile(join(root, name), contents)));
  const server = await startStaticServer(root, 0);
  servers.push(server);
  return { root, server };
}

function runCli(root, port) {
  return new Promise((resolve, reject) => {
    const child = spawn(process.execPath, [fileURLToPath(new URL("./serve.mjs", import.meta.url)), root, String(port)], {
      encoding: "utf8",
    });
    let stderr = "";
    child.stderr.setEncoding("utf8");
    child.stderr.on("data", (chunk) => { stderr += chunk; });
    child.once("error", reject);
    child.once("close", (status) => resolve({ status, stderr }));
  });
}
