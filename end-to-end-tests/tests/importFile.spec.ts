import { test, expect } from "@playwright/test";
import { PageManager } from "../page-objects/pageManager";
import * as os from "os";

test("Import file success", async ({ page }) => {
    const filePath = `${os.homedir()}/workspace/diwi/end-to-end-tests/excel-importer.xlsx`;
    const pm = new PageManager(page);
    await pm.navigateTo().loginPage();
    await pm.navigateTo().importExcelPage();
    await page.locator('input[type="file"]').setInputFiles(filePath);
    await expect(page).toHaveURL("http://localhost:3000/projects/table");
});

test("Import file failed", async ({ page }) => {
    const filePath = `${os.homedir()}/workspace/diwi/end-to-end-tests/excel-false.xlsx`;
    const pm = new PageManager(page);
    await pm.navigateTo().loginPage();
    await pm.navigateTo().importExcelPage();
    await page.locator('input[type="file"]').setInputFiles(filePath);
    const rowError= await page.locator("tr").last().locator("td").nth(0).innerText();
    const columnError= await page.locator("tr").last().locator("td").nth(1).innerText();
    const valueError= await page.locator("tr").last().locator("td").nth(2).innerText();
    const DescriptionError= await page.locator("tr").last().locator("td").nth(3).innerText();
    await expect(rowError).toContain("4");
    await expect(columnError).toContain("");
    await expect(valueError).toContain("Plan soort");
    await expect(DescriptionError).toContain("The excel file does not contain all expected headers.");
});
