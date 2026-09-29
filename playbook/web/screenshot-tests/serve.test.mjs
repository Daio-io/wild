import test from "node:test";
import assert from "node:assert/strict";
import { mkdtemp, rm, writeFile } from "node:fs/promises";
import { request } from "node:http";
import { tmpdir } from "node:os";
import { join } from "node:path";

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
  const root = await mkdtemp(join(tmpdir(), "screenshot-server-"));
  await writeFile(join(root, "index.html"), "home");
  const server = await startStaticServer(root, 0);
  servers.push(server);
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
  const root = await mkdtemp(join(tmpdir(), "screenshot-server-"));
  await writeFile(join(root, "index.html"), "home");
  const server = await startStaticServer(root, 0);
  servers.push(server);
  const { port } = server.address();

  const response = await fetch(`http://127.0.0.1:${port}/missing-route`);
  assert.equal(response.status, 200);
  assert.equal(response.headers.get("content-type"), "text/html");
  assert.equal(await response.text(), "home");

  await rm(root, { recursive: true, force: true });
});

test("serves JavaScript and Wasm with explicit MIME types", async () => {
  const root = await mkdtemp(join(tmpdir(), "screenshot-server-"));
  await writeFile(join(root, "app.js"), "console.log('app')");
  await writeFile(join(root, "app.wasm"), "wasm");
  const server = await startStaticServer(root, 0);
  servers.push(server);
  const { port } = server.address();

  const jsResponse = await fetch(`http://127.0.0.1:${port}/app.js`);
  const wasmResponse = await fetch(`http://127.0.0.1:${port}/app.wasm`);
  assert.equal(jsResponse.headers.get("content-type"), "application/javascript");
  assert.equal(wasmResponse.headers.get("content-type"), "application/wasm");

  await rm(root, { recursive: true, force: true });
});

test("rejects startup when the port is occupied", async () => {
  const root = await mkdtemp(join(tmpdir(), "screenshot-server-"));
  await writeFile(join(root, "index.html"), "home");
  const first = await startStaticServer(root, 0);
  servers.push(first);
  const { port } = first.address();

  await assert.rejects(startStaticServer(root, port), { code: "EADDRINUSE" });

  await rm(root, { recursive: true, force: true });
});

function requestForTraversal(port, resolve, reject) {
  return request({ host: "127.0.0.1", port, path: "/%2e%2e/package.json" }, (response) => {
    response.resume();
    response.once("end", () => resolve(response.statusCode));
  }).on("error", reject);
}
