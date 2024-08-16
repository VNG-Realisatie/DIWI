import test, { expect } from "@playwright/test";
import { PageManager } from "../page-objects/pageManager";

test("Add municipality in to the custom properties", async ({ page }) => {
    const pm = new PageManager(page);
    await pm.navigateTo().loginPage();
    await pm.navigateTo().customPropertiesPage();

    const district = page.locator("#municipality");
    await district.click();
    const dialog = await page.locator("role=dialog").locator("input").count();

    const inputExists = dialog > 3;

    if (!inputExists) {
        await page.getByRole("dialog").getByTestId("AddCircleIcon").click();
        const categoryInput = page.locator('input[placeholder="Voeg maatwerk eigenschap toe"]');
        await categoryInput.fill("Groningen");
        await page.locator("#save-custom-property").click();
    } else {
        await page.locator("#cancel-custom-property").click();
    }
});
