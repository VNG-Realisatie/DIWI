import test, { expect } from "@playwright/test";
import { PageManager } from "../page-objects/pageManager";

test("Add boolean in to the custom properties", async ({ page }) => {
    const pm = new PageManager(page);
    await pm.navigateTo().loginPage();
    await pm.navigateTo().customPropertiesPage();

    const addIcon = page.getByTestId("AddCircleIcon");
    await addIcon.click();
    const dialog = await page.locator("role=dialog");
    const name = dialog.locator("input").first();
    await name.fill("Betaald?");
    const projectOrHouseBlock = dialog.locator("input").nth(1);
    await projectOrHouseBlock.click({ force: true });
    await page.locator("li").first().click();
    await dialog.getByText("Open tekstveld").click();
    await page.locator("li").first().click();

    await page.locator("#save-custom-property").click();
});
