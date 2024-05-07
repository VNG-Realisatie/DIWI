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
    const errorIcon = await page.locator('[data-testid="ErrorOutlineIcon"]').first();
    await expect(errorIcon).toBeVisible();
});
