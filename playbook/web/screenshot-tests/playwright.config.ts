import { defineConfig } from "@playwright/test";

export default defineConfig({
  testDir: "./tests",
  fullyParallel: false,
  retries: 0,
  use: {
    viewport: { width: 1280, height: 720 },
    colorScheme: "dark",
  },
  expect: {
    toHaveScreenshot: {
      animations: "disabled",
      maxDiffPixelRatio: 0.001,
    },
  },
  snapshotPathTemplate: "{testDir}/../screenshots/{projectName}/{arg}{ext}",
  projects: [
    { name: "js", use: { baseURL: "http://127.0.0.1:4173" } },
    { name: "wasm", use: { baseURL: "http://127.0.0.1:4174" } },
  ],
  webServer: [
    { command: "node serve.mjs ../build/dist/js/productionExecutable 4173", port: 4173 },
    { command: "node serve.mjs ../build/dist/wasmJs/productionExecutable 4174", port: 4174 },
  ],
});
