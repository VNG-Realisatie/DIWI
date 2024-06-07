import { test, expect } from "@playwright/test";
import { PageManager } from "../page-objects/pageManager";
import customProjectProperties from "../test-data/customProjectProperties.json";

test.describe("Create project page", () => {
    let projectId;
    test.beforeEach(async ({ page }) => {
        await page.route("*/**/rest/properties?objectType=PROJECT", async (route) => {
            await route.fulfill({
                body: JSON.stringify(customProjectProperties),
            });
        });
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
        await projectNameWarning.getByRole("textbox").fill("Test project");
        await startDate.getByRole("textbox").fill("01-01-2021");
        await endDate.getByRole("textbox").fill("01-01-2025");
        expect(await projectNameWarning.textContent()).not.toContain("Vul het veld projectnaam in");
        expect(await startDate.textContent()).not.toContain("Vul het veld startdatum in");
        expect(await endDate.textContent()).not.toContain("Vul het veld einddatum in");
    });

    test("Fill project details and save", async ({ page }) => {
        const projectNameWarning = await page.getByTestId("input-label-stack").first();
        const startDate = await page.getByTestId("input-label-stack").nth(2);
        const endDate = await page.getByTestId("input-label-stack").nth(3);
        const planType = await page.getByTestId("input-label-stack").nth(1);
        const priority = await page.getByTestId("input-label-stack").nth(4);
        const projectPhase = await page.getByTestId("input-label-stack").nth(5);
        const municipalityRole = await page.getByTestId("input-label-stack").nth(6);
        const confidentialityLevel = await page.getByTestId("input-label-stack").nth(7);
        const planStatus = await page.getByTestId("input-label-stack").nth(8);
        const municipality = await page.getByTestId("input-label-stack").nth(9);
        const wijk = await page.getByTestId("input-label-stack").nth(10);
        const buurt = await page.getByTestId("input-label-stack").nth(11);
        const owner = await page.getByRole("combobox").nth(4);
        const customCatProperty = await page.getByRole("combobox").nth(10);
        const customBooleanProperty = await page.getByRole("combobox").nth(11);

        await projectNameWarning.getByRole("textbox").fill("Test project");
        await startDate.getByRole("textbox").fill("01-01-2021");
        await endDate.getByRole("textbox").fill("01-01-2025");
        await planType.getByRole("combobox").fill("Verdichting");
        await page.getByText("Verdichting").click();
        await priority.getByRole("combobox").fill("Hoog");
        await page.getByText("Hoog").click();
        await projectPhase.getByRole("combobox").fill("3 Definitie");
        await page.getByText("3 Definitie").click();
        await municipalityRole.getByRole("combobox").fill("Vergunningverlener");
        await page.getByText("Vergunningverlener").click();
        await confidentialityLevel.getByRole("combobox").fill("Intern raad");
        await page.getByText("Intern raad").click();
        await owner.fill("A");
        await page.getByText("Ad Min").click();
        await planStatus.getByRole("combobox").fill("2A Vastgesteld");
        await page.getByText("2A Vastgesteld").click();
        await customCatProperty.fill("tom");
        await page.getByText("tom").click();
        await customBooleanProperty.fill("Ja");
        await page.getByText("Ja").click();
        await page.locator('input[type="text"]').nth(15).fill("test");
        await page.getByText("Opslaan").first().click();
        await page.waitForTimeout(1000);
        await page.getByText("Opslaan en volgende").click();
        await page.waitForTimeout(1000);
        projectId = page.url().split("/")[5];

        expect(projectId).not.toBeNull();
        expect(await page.getByTestId("AddCircleIcon")).toBeVisible();

        await page.locator("#panel1-header").click();
    });
});
