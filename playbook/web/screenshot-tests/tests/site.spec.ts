import { expect, test } from "@playwright/test";
import { gotoReadyCanvas } from "./support/gotoReadyCanvas";

// Production browser navigation is hash-based (`bindToBrowserNavigation`).
const routes = [
  ["getting-started", "/#getting-started"],
  ["button", "/#components/button"],
  ["text-field", "/#components/text-field"],
  ["icon", "/#components/icon"],
  ["style", "/#foundations/style"],
] as const;

for (const [name, path] of routes) {
  test(name, async ({ page }) => {
    await gotoReadyCanvas(page, path);
    await expect(page).toHaveScreenshot(`${name}.png`);
  });
}
