import { createServer } from "node:http";
import { readFile, stat } from "node:fs/promises";
import { extname, join, resolve, sep } from "node:path";
import { fileURLToPath } from "node:url";

const MIME_TYPES = {
  ".css": "text/css",
  ".html": "text/html",
  ".js": "application/javascript",
  ".png": "image/png",
  ".wasm": "application/wasm",
};

export function resolveRequestPath(root, requestUrl) {
  const rawPathname = requestUrl.split("?", 1)[0] || "/";
  const pathname = decodeURIComponent(rawPathname);
  const rootPath = resolve(root);
  const requestedPath = resolve(rootPath, `.${pathname}`);
  if (requestedPath !== rootPath && !requestedPath.startsWith(`${rootPath}${sep}`)) {
    const error = new Error("Request path escapes server root");
    error.code = "TRAVERSAL";
    throw error;
  }
  return requestedPath;
}

export function contentType(filePath) {
  return MIME_TYPES[extname(filePath).toLowerCase()] ?? "application/octet-stream";
}

export async function startStaticServer(root, port) {
  const rootStats = await stat(root).catch(() => null);
  if (!rootStats?.isDirectory()) {
    throw new Error(`Static server root does not exist: ${root}`);
  }

  const server = createServer(async (request, response) => {
    try {
      const requestedPath = resolveRequestPath(root, request.url ?? "/");
      const requestedStats = await stat(requestedPath).catch(() => null);
      const filePath = requestedStats?.isFile() ? requestedPath : join(root, "index.html");
      const body = await readFile(filePath);
      response.writeHead(200, { "Content-Type": contentType(filePath) });
      response.end(body);
    } catch (error) {
      if (error.code === "TRAVERSAL" || error instanceof URIError) {
        response.writeHead(403);
      } else {
        response.writeHead(404);
      }
      response.end();
    }
  });
  await new Promise((resolve, reject) => {
    const onError = (error) => {
      server.off("listening", onListening);
      reject(error);
    };
    const onListening = () => {
      server.off("error", onError);
      resolve();
    };
    server.once("error", onError);
    server.once("listening", onListening);
    server.listen(port, "127.0.0.1");
  });
  return server;
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  const [, , root, port] = process.argv;
  const moduleDirectory = import.meta.dirname ?? fileURLToPath(new URL(".", import.meta.url));
  startStaticServer(resolve(moduleDirectory, root), Number(port)).catch((error) => {
    console.error(error.message);
    process.exitCode = 1;
  });
}
