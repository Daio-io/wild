import { expect, test } from "@playwright/test";

test("getting started renders", async ({ page }) => {
  await page.goto("/getting-started");
  const canvas = page.locator("canvas");
  await canvas.waitFor({ state: "visible" });
  await page.evaluate(() => document.fonts.ready);
  await expect
    .poll(() =>
      canvas.evaluate(
        (node) => node instanceof HTMLCanvasElement && node.width > 0 && node.height > 0,
      ),
    )
    .toBe(true);
  await expect(page).toHaveScreenshot("getting-started.png", { fullPage: false });
});
