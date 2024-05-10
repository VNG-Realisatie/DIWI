import { test, expect } from "@playwright/test";
import { PageManager } from "../page-objects/pageManager";

test.beforeEach(async ({ page }) => {
    const pm = new PageManager(page);
    await pm.navigateTo().loginPage();
    await pm.navigateTo().importExcelPage();
});

test("Import file success", async ({ page }) => {
    const filePath = `excel-importer.xlsx`;

    await page.locator('input[type="file"]').setInputFiles(filePath);
    await expect(page).toHaveURL("http://localhost:3000/projects/table");
});

test("Import file failed", async ({ page }) => {
    const filePath = `excel-false.xlsx`;

    await page.locator('input[type="file"]').setInputFiles(filePath);

    const rowError = await page.locator("tr").last().locator("td").nth(0).innerText();
    const columnError = await page.locator("tr").last().locator("td").nth(1).innerText();
    const valueError = await page.locator("tr").last().locator("td").nth(2).innerText();
    const DescriptionError = await page.locator("tr").last().locator("td").nth(3).innerText();

    expect(rowError).toContain("4");
    expect(columnError).toContain("");
    expect(valueError).toContain("Plan soort");
    expect(DescriptionError).toContain("The excel file does not contain all expected headers.");
});
