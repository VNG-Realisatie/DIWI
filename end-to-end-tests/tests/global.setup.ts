import test, { expect } from "@playwright/test";
import { PageManager } from "../page-objects/pageManager";

test("give admin user user plus role", async ({ page }) => {
    const pm = new PageManager(page);
    await pm.navigateTo().loginPage();

    await page.getByLabel("open drawer").click();
    await page.getByRole("button", { name: "Gebruikers" }).click();

    await page.getByRole("row", { name: "admin@example.com" }).getByTestId("EditOutlinedIcon").click();

    const userTypeSelector = page.getByRole("combobox");
    if ((await userTypeSelector.inputValue()) === "Hoofdgebruiker") {
        await page.getByRole("button", { name: "Annuleren" }).click();
    } else {
        await userTypeSelector.click();
        await page.getByRole("option", { name: "Hoofdgebruiker" }).click();
        await page.getByRole("button", { name: "Opslaan" }).click();
    }
});
