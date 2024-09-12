import test from "@playwright/test";
import { PageManager } from "../page-objects/pageManager";

test("Add neighborhood in to the custom properties", async ({ page }) => {
    const pm = new PageManager(page);
    await pm.navigateTo().loginPage();
    await pm.navigateTo().customPropertiesPage();

    const district = page.locator("#neighbourhood");
    await district.click();

    await page.getByRole("dialog").getByTestId("AddCircleIcon").click();
    const categoryInput = page.locator('input[placeholder="Voeg maatwerk eigenschap toe"]');
    await categoryInput.fill("Oosterpoort");
    await page.locator("#save-custom-property").click();
});
