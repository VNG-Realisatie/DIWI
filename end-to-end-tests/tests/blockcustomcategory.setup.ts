import test, { expect } from "@playwright/test";
import { PageManager } from "../page-objects/pageManager";

test("Add category in to the block custom properties", async ({ page }) => {
    const pm = new PageManager(page);
    await pm.navigateTo().loginPage();
    await pm.navigateTo().customPropertiesPage();

    const addIcon = page.getByTestId("AddCircleIcon");
    await addIcon.click();
    const dialog = await page.locator("role=dialog");
    const name = dialog.locator("input").first();
    await name.fill("ToeStemmingblock");
    const projectOrHouseBlock = dialog.locator("input").nth(0);
    await projectOrHouseBlock.click({ force: true });
    await page.locator("li").first().click();
    await dialog.getByText("Open tekstveld").click();
    await page.locator("li").nth(1).click();

    await dialog.getByTestId("AddCircleIcon").click();
    const categoryInput = page.locator('input[placeholder="Voeg maatwerk eigenschap toe"]');
    await categoryInput.fill("Bewoners");
    await page.locator("#save-custom-property").click();
});
