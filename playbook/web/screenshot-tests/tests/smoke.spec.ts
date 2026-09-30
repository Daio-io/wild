import { expect, test } from "@playwright/test";
import { gotoReadyCanvas } from "./support/gotoReadyCanvas";

test("getting started renders", async ({ page }) => {
  await gotoReadyCanvas(page, "/#getting-started");
  await expect(page).toHaveScreenshot("getting-started.png", { fullPage: false });
});
