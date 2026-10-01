import { expect, type Page } from "@playwright/test";

export async function gotoReadyCanvas(page: Page, path: string) {
  const response = await page.goto(path, { waitUntil: "networkidle" });
  expect(response?.ok()).toBe(true);
  await page.locator("canvas").waitFor({ state: "visible" });
  await page.evaluate(() => document.fonts.ready);
  // ComposeViewport mounts the Skiko canvas inside an open shadow root, so
  // document.querySelector("canvas") misses it; walk light + shadow trees.
  await page.waitForFunction(() => {
    const findCanvas = (root: ParentNode): HTMLCanvasElement | null => {
      for (const node of root.querySelectorAll("*")) {
        if (node instanceof HTMLCanvasElement) {
          return node;
        }
        if (node.shadowRoot) {
          const nested = findCanvas(node.shadowRoot);
          if (nested) {
            return nested;
          }
        }
      }
      return null;
    };
    const canvas = findCanvas(document);
    return canvas instanceof HTMLCanvasElement && canvas.width > 0 && canvas.height > 0;
  });
  await page.evaluate(
    () =>
      new Promise<void>((resolve) =>
        requestAnimationFrame(() => requestAnimationFrame(() => resolve())),
      ),
  );
}
