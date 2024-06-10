import { test, expect } from "@playwright/test";
import { PageManager } from "../page-objects/pageManager";
import customProjectProperties from "../test-data/customProjectProperties.json";
import houseBlockCustomProperties from "../test-data/houseBlockCustomProperties.json";

test.describe("Create project page", () => {
    let projectId;
    test.beforeEach(async ({ page }) => {
        await page.route("*/**/rest/properties?objectType=PROJECT", async (route) => {
            await route.fulfill({
                body: JSON.stringify(customProjectProperties),
            });
        });
        await page.route("*/**/rest/properties?objectType=WONINGBLOK", async (route) => {
            await route.fulfill({
                body: JSON.stringify(houseBlockCustomProperties),
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
        const projectNameWarning = page.getByTestId("input-label-stack").first();
        const projectColor = page.locator(".project-color-selector");
        const startDate = page.getByTestId("input-label-stack").nth(2);
        const endDate = page.getByTestId("input-label-stack").nth(3);
        const planType = page.getByTestId("input-label-stack").nth(1);
        const priority = page.getByTestId("input-label-stack").nth(4);
        const projectPhase = page.getByTestId("input-label-stack").nth(5);
        const municipalityRole = page.getByTestId("input-label-stack").nth(6);
        const confidentialityLevel = page.getByTestId("input-label-stack").nth(7);
        const planStatus = page.getByTestId("input-label-stack").nth(8);
        const municipality = page.getByTestId("input-label-stack").nth(9);
        const wijk = page.getByTestId("input-label-stack").nth(10);
        const buurt = page.getByTestId("input-label-stack").nth(11);
        const owner = page.getByRole("combobox").nth(4);
        const customCatProperty = page.getByRole("combobox").nth(10);
        const customBooleanProperty = page.getByRole("combobox").nth(11);

        await projectNameWarning.getByRole("textbox").fill("Test project");
        await projectColor.click();

        const selectColor = await page.locator(".block-picker > div:nth-child(3) > div:nth-child(1) > span:nth-child(1) > div:nth-child(1)");
        await selectColor.click();

        await page.mouse.click(0, 500);
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
    });

    test("House block check mandatory fields warning texts", async ({ page }) => {
        await page.goto(`http://localhost:3000/project/create/${projectId}/blocks`);
        //House Blocks tab
        await page.locator("#panel1-header").click();
        //Check mandatory fields
        const houseBlockName = page.getByTestId("input-label-stack").first();
        const mutationAmount = page.getByTestId("input-label-stack").nth(4);
        const mutationType = page.getByTestId("input-label-stack").nth(5);
        expect(await houseBlockName.textContent()).toContain("Vul het veld projectnaam in");
        expect(await mutationAmount.textContent()).toContain("​Vul het veld aantal in met een positief getal");
        expect(await mutationType.textContent()).toContain("​Vul het veld mutatiesoort in");
        await houseBlockName.getByRole("textbox").fill("Test jouse block");
        await page.locator("#Aantal").fill("10");
        await mutationType.getByRole("combobox").fill("Sloop");
        await page.getByText("Sloop").click();
        expect(await houseBlockName.textContent()).not.toContain("Vul het veld projectnaam in");
        expect(await mutationAmount.textContent()).not.toContain("​Vul het veld aantal in met een positief getal");
        expect(await mutationType.textContent()).not.toContain("​Vul het veld mutatiesoort in");
    });

    test("Create house block ", async ({ page }) => {
        await page.goto(`http://localhost:3000/project/create/${projectId}/blocks`);
        //House Blocks tab
        await page.locator("#panel1-header").click();
        //Check mandatory fields
        const houseBlockName = page.getByTestId("input-label-stack").first();
        const size = page.getByTestId("input-label-stack").nth(1).locator("#size");
        const startDate = page.getByTestId("input-label-stack").nth(2);
        const endDate = page.getByTestId("input-label-stack").nth(3);
        const mutationAmount = page.getByTestId("input-label-stack").nth(4).locator("#Aantal");
        const mutationType = page.getByTestId("input-label-stack").nth(5);
        //OwnershipInformationGroup
        const houseBlockAmount = page.locator(".ownership-house-amount input");
        const houseBlockValue = page.locator(".ownership-house-value input");
        const houseBlockRent = page.locator(".ownership-house-rent input");
        //PhysicalAppeareanceGroup
        const gallerijflat = page.locator("#Gallerijflat");
        const hoekwoning = page.locator("#Hoekwoning");
        const portiekflat = page.locator("#Portiekflat");
        const tussenwoning = page.locator("#Tussenwoning");
        const tweeOnderEenKap = page.locator("#Tweeondereenkap");
        const vrijstaand = page.locator("#Vrijstaand");
        //TargetGroup
        const ghz = page.locator("#GHZ");
        const groteGezinnen = page.locator("#Grotegezinnen");
        const jongeren = page.locator("#Jongeren");
        const ouderen = page.locator("#Ouderen");
        const regulier = page.locator("#Regulier");
        const student = page.locator("#Student");
        //HouseTypeGroup
        const meergezinswoning = page.locator("#Meergezinswoning");
        const eengezinswoning = page.locator("#Eengezinswoning");
        //GroundPositionGroup
        const noPermissionFromLandOwner = page.locator("#Geentoestemminggrondeigenaar");
        const permissionFromLandOwner = page.locator("#Intentiemedewerkinggrondeigenaar");
        const formalPermissionFromLandOwner = page.locator("#Formeletoestemmingvangrondeigenaar");
        //Programming
        await page.getByRole("radio", { name: "Ja" }).check({ force: true });

        //Custom Properties
        const booleanCustomProperty = page.locator("#boolean-custom-property");
        const categoryCustomProperty = page.locator("#category-custom-property");
        const textCustomProperty = page.locator("#text-custom-property");

        //Fill all values
        await houseBlockName.getByRole("textbox").fill("Test house block");
        await size.fill("100");
        await startDate.getByRole("textbox").fill("01-01-2021");
        await endDate.getByRole("textbox").fill("01-01-2025");
        await mutationAmount.fill("10");
        await mutationType.getByRole("combobox").fill("Sloop");
        await page.getByText("Sloop").click();
        await houseBlockAmount.fill("1");
        await houseBlockValue.fill("100000");
        await houseBlockRent.fill("500");

        await gallerijflat.fill("1");
        await hoekwoning.fill("1");
        await portiekflat.fill("1");
        await tussenwoning.fill("1");
        await tweeOnderEenKap.fill("1");
        await vrijstaand.fill("1");

        await ghz.fill("1");
        await groteGezinnen.fill("1");
        await jongeren.fill("1");
        await ouderen.fill("1");
        await regulier.fill("1");
        await student.fill("1");

        await meergezinswoning.fill("1");
        await eengezinswoning.fill("1");

        await noPermissionFromLandOwner.fill("1");
        await permissionFromLandOwner.fill("1");
        await formalPermissionFromLandOwner.fill("1");

        await booleanCustomProperty.fill("Nee");
        await page.getByText("Nee").nth(1).click();
        await categoryCustomProperty.fill("Build");
        await page.getByText("Build").click();
        await textCustomProperty.fill("Test");

        await page.getByText("Opslaan en volgende").click();
        await page.waitForTimeout(1000);
        const urlMapText = await page.url().split("/")[6];
        expect(urlMapText).toContain("map");
    });
});
