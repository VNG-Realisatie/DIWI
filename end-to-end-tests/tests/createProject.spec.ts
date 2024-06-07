import { test, expect } from "@playwright/test";
import { PageManager } from "../page-objects/pageManager";

test.describe("Create project page", () => {
    test.beforeEach(async ({ page }) => {
        const pm = new PageManager(page);
        await pm.navigateTo().loginPage();
        await pm.navigateTo().projectCreatePage();
    });

    test("Check Mandatory fields warning texts", async ({ page }) => {
        const projectNameWarning = await page.getByTestId("input-label-stack").first();
        const startDate = await page.getByTestId("input-label-stack").nth(2);
        const endDate = await page.getByTestId("input-label-stack").nth(3);

        expect(await projectNameWarning.textContent()).toContain("Vul het veld projectnaam in");
        expect(await startDate.textContent()).toContain("Vul het veld startdatum in");
        expect(await endDate.textContent()).toContain("Vul het veld einddatum in");
    });
});
