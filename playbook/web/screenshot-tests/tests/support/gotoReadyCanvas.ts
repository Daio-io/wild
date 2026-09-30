import { expect, type Page } from "@playwright/test";

export async function gotoReadyCanvas(page: Page, path: string) {
  const response = await page.goto(path, { waitUntil: "networkidle" });
  expect(response?.ok()).toBe(true);
  await page.locator("canvas").waitFor({ state: "visible" });
  await page.evaluate(() => document.fonts.ready);
  await page.evaluate(
    () =>
      new Promise<void>((resolve) =>
        requestAnimationFrame(() => requestAnimationFrame(() => resolve())),
      ),
  );
}
