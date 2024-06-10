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
        //General Information Group
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

    test("Add parcel in to the map", async ({ page }) => {
        await page.goto(`http://localhost:3000/project/create/${projectId}/map`);
        const zoomIcon = page.locator(".ol-zoom-in");
        await zoomIcon.click();
        await page.waitForTimeout(500);
        await zoomIcon.click();
        await page.waitForTimeout(500);
        await zoomIcon.click();
        await page.waitForTimeout(500);
        await zoomIcon.click();
        await page.waitForTimeout(500);
        await zoomIcon.click();
        await page.waitForTimeout(500);
        await zoomIcon.click();
        await page.waitForTimeout(500);
        await zoomIcon.click();
        await page.mouse.click(300, 300);
        await page.getByText("Opslaan en volgende").click();
        await page.waitForTimeout(1000);
        const urlMapText = await page.url().split("/")[5];
        expect(urlMapText).toContain("characteristics");
    });
    test("Check characteristics page", async ({ page }) => {
        await page.goto(`http://localhost:3000/projects/${projectId}/characteristics`);
        const projectName = page.locator(".project-name input");
        expect(await projectName.inputValue()).toContain("Test project");
        const projectPlanType = page.locator(".project-plantype span").first();
        expect(await projectPlanType.textContent()).toContain("Verdichting");
        const startDate = page.locator(".project-startdate input");
        expect(await startDate.inputValue()).toContain("01-01-2021");
        const endDate = page.locator(".project-enddate input");
        expect(await endDate.inputValue()).toContain("01-01-2025");
        const projectPriority = page.locator(".project-priority input");
        expect(await projectPriority.inputValue()).toContain("Hoog");
        const projectPhase = page.locator(".project-phase input");
        expect(await projectPhase.inputValue()).toContain("3 Definitie");
        const projectMunicipalityRole = page.locator(".project-municipality-role span").first();
        expect(await projectMunicipalityRole.textContent()).toContain("Vergunningverlener");
        const projectConfidentialityLevel = page.locator(".project-confidentiality input");
        expect(await projectConfidentialityLevel.inputValue()).toContain("Intern raad");
        const projectOwner = page.locator(".project-owner .MuiAvatar-circular");
        expect(await projectOwner.textContent()).toContain("AM");
        const projectPlanStatus = page.locator(".project-planning-status span").first();
        expect(await projectPlanStatus.textContent()).toContain("2A Vastgesteld");
        const projectCustomCatProperty = page.locator(".CATEGORY span").first();
        expect(await projectCustomCatProperty.textContent()).toContain("tom");
        const projectCustomBooleanProperty = page.locator(".BOOLEAN input").first();
        expect(await projectCustomBooleanProperty.inputValue()).toContain("Ja");
        const projectCustomTextProperty = page.locator(".TEXT input").first();
        expect(await projectCustomTextProperty.inputValue()).toContain("test");
        await page.locator("#panel1-header").nth(1).click();
        const houseBlockName = page.getByTestId("input-label-stack").nth(12).locator("input");
        expect(await houseBlockName.inputValue()).toContain("Test house block");
        const size = page.getByTestId("input-label-stack").nth(13).locator("#size");
        expect(await size.inputValue()).toContain("100");
        const houseBlockStartDate = page.getByTestId("input-label-stack").nth(14).locator("input");
        expect(await houseBlockStartDate.inputValue()).toContain("01-01-2021");
        const houseBlockEndDate = page.getByTestId("input-label-stack").nth(15).locator("input");
        expect(await houseBlockEndDate.inputValue()).toContain("01-01-2025");
        const mutationAmount = page.getByTestId("input-label-stack").nth(16).locator("#Aantal");
        expect(await mutationAmount.inputValue()).toContain("10");
        const mutationType = page.getByTestId("input-label-stack").nth(17).locator("input");
        expect(await mutationType.inputValue()).toContain("Sloop");
        const houseBlockAmount = page.locator(".ownership-house-amount >div>p");
        expect(await houseBlockAmount.textContent()).toContain("1");
        const houseBlockValue = page.locator(".ownership-house-value input");
        expect(await houseBlockValue.inputValue()).toContain("100000");
        const houseBlockRent = page.locator(".ownership-house-rent input");
        expect(await houseBlockRent.inputValue()).toBe("");
        const gallerijflat = page.locator("#Gallerijflat");
        expect(await gallerijflat.inputValue()).toContain("1");
        const hoekwoning = page.locator("#Hoekwoning");
        expect(await hoekwoning.inputValue()).toContain("1");
        const portiekflat = page.locator("#Portiekflat");
        expect(await portiekflat.inputValue()).toContain("1");
        const tussenwoning = page.locator("#Tussenwoning");
        expect(await tussenwoning.inputValue()).toContain("1");
        const tweeOnderEenKap = page.locator("#Tweeondereenkap");
        expect(await tweeOnderEenKap.inputValue()).toContain("1");
        const vrijstaand = page.locator("#Vrijstaand");
        expect(await vrijstaand.inputValue()).toContain("1");
        const ghz = page.locator("#GHZ");
        expect(await ghz.inputValue()).toContain("1");
        const groteGezinnen = page.locator("#Grotegezinnen");
        expect(await groteGezinnen.inputValue()).toContain("1");
        const jongeren = page.locator("#Jongeren");
        expect(await jongeren.inputValue()).toContain("1");
        const ouderen = page.locator("#Ouderen");
        expect(await ouderen.inputValue()).toContain("1");
        const regulier = page.locator("#Regulier");
        expect(await regulier.inputValue()).toContain("1");
        const student = page.locator("#Student");
        expect(await student.inputValue()).toContain("1");
        const meergezinswoning = page.locator("#Meergezinswoning");
        expect(await meergezinswoning.inputValue()).toContain("1");
        const eengezinswoning = page.locator("#Eengezinswoning");
        expect(await eengezinswoning.inputValue()).toContain("1");
        const noPermissionFromLandOwner = page.locator("#Geentoestemminggrondeigenaar");
        expect(await noPermissionFromLandOwner.inputValue()).toContain("1");
        const permissionFromLandOwner = page.locator("#Intentiemedewerkinggrondeigenaar");
        expect(await permissionFromLandOwner.inputValue()).toContain("1");
        const formalPermissionFromLandOwner = page.locator("#Formeletoestemmingvangrondeigenaar");
        expect(await formalPermissionFromLandOwner.inputValue()).toContain("1");
        const booleanCustomProperty = page.locator("#boolean-custom-property").nth(1);
        expect(await booleanCustomProperty.inputValue()).toContain("Nee");
        const categoryCustomProperty = page.locator(".house-block-custom-properties span").nth(1);
        expect(await categoryCustomProperty.textContent()).toContain("Build");
        const textCustomProperty = page.locator("#text-custom-property").first();
        expect(await textCustomProperty.inputValue()).toContain("test");
    });
    test("Delete Created Project", async ({ page }) => {
        await page.goto(`http://localhost:3000/projects/${projectId}/characteristics`);
        await page.getByTestId("DeleteForeverOutlinedIcon").click();
        await page.getByText("Ja").nth(1).click();
        await page.waitForTimeout(1000);
        const urlText = await page.url();
        expect(urlText).toContain("projects/table");
    });
});
