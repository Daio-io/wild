import { expect, test } from "@playwright/test";

test("getting started renders", async ({ page }) => {
  await page.goto("/getting-started");
  const canvas = page.locator("canvas");
  await canvas.waitFor({ state: "visible" });
  await page.evaluate(() => document.fonts.ready);
  await page.waitForFunction(() => {
    const canvas = document.querySelector("canvas");
    return canvas instanceof HTMLCanvasElement && canvas.width > 0 && canvas.height > 0;
  });
  await expect(page).toHaveScreenshot("getting-started.png", { fullPage: false });
});
