import { expect, type Page } from "@playwright/test";

export async function gotoReadyCanvas(page: Page, path: string) {
  const response = await page.goto(path, { waitUntil: "networkidle" });
  expect(response?.ok()).toBe(true);
  const canvas = page.locator("canvas");
  await canvas.waitFor({ state: "visible" });
  await page.evaluate(() => document.fonts.ready);
  // Compose mounts the Skia canvas inside an open shadow root; pierce via locator.
  await expect
    .poll(async () => {
      const size = await canvas.evaluate((el) => ({
        width: (el as HTMLCanvasElement).width,
        height: (el as HTMLCanvasElement).height,
      }));
      return size.width > 0 && size.height > 0;
    })
    .toBe(true);
  await page.evaluate(
    () =>
      new Promise<void>((resolve) =>
        requestAnimationFrame(() => requestAnimationFrame(() => resolve())),
      ),
  );
}
