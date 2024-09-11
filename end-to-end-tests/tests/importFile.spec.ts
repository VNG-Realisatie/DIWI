import { test, expect } from "@playwright/test";
import { PageManager } from "../page-objects/pageManager";
import * as path from "path";

test.beforeEach(async ({ page }) => {
    const pm = new PageManager(page);
    await pm.navigateTo().loginPage();
    await pm.navigateTo().importExcelPage();
});

test("Import file success", async ({ page }) => {
    const filePath = `./excel-importer.xlsx`;
    const fileChooserPromise = page.waitForEvent("filechooser");
    const inputFile = await page.locator("#upload-stack");
    inputFile.click();
    const fileChooser = await fileChooserPromise;
    await fileChooser.setFiles(filePath);
    await expect(page).toHaveURL("http://localhost:3000/projects/table?pageNumber=1&pageSize=10&");
});

test("Import file failed", async ({ page }) => {
    const filePath = `./excel-false.xlsx`;

    const fileChooserPromise = page.waitForEvent("filechooser");
    const inputFile = await page.locator("#upload-stack");
    inputFile.click();
    const fileChooser = await fileChooserPromise;
    await fileChooser.setFiles(filePath);

    const rowError = await page.locator(".rij p").innerText();
    const valueError = await page.locator(".waarde p").last().innerText();
    const descriptionError = await page.locator(".import-error").innerText();

    expect(rowError).toContain("3");

    expect(valueError).toContain("Plan soort");
    expect(descriptionError).toContain("Het Excel-bestand bevat niet alle verwachte koppen.");
});
