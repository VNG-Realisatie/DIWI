import { test, expect } from "@playwright/test";
import { PageManager } from "../page-objects/pageManager";
import * as path from "path";

test.beforeEach(async ({ page }) => {
    const pm = new PageManager(page);
    await pm.navigateTo().loginPage();
    await pm.navigateTo().importExcelPage();
});

test("Import file success", async ({ page }) => {
    const filePath = `end-to-end-tests/excel-importer.xlsx`;

    await page.locator('input[type="file"]').setInputFiles(filePath);
    await expect(page).toHaveURL("http://localhost:3000/projects/table");
});

test("Import file failed", async ({ page }) => {
    const filePath = `end-to-end-tests/excel-false.xlsx`;

    await page.locator('input[type="file"]').setInputFiles(filePath);

    const rowError = await page.locator(".rij p").innerText();
    const valueError = await page.locator(".waarde p").last().innerText();
    const descriptionError = await page.locator(".import-error").innerText();

    expect(rowError).toContain("3");

    expect(valueError).toContain("Plan soort");
    expect(descriptionError).toContain("Het Excel-bestand bevat niet alle verwachte koppen.");
});
