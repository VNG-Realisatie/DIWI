import { Locator, Page, expect } from "@playwright/test";
import { HelperBase } from "./helperBase";

export class NavigationPage extends HelperBase {
    // readonly page: Page;
    readonly fromLayoutsMenuItem: Locator;

    constructor(page: Page) {
        super(page);
    }

    async loginPage() {
        await this.page.goto("http://localhost:3000/rest/auth/login");

        await this.page.getByLabel("Username or email").click();
        await this.page.getByLabel("Username or email").fill("admin");
        await this.page.getByLabel("Password", { exact: true }).click();
        await this.page.getByLabel("Password", { exact: true }).fill("admin");
        await this.page.getByLabel("Password", { exact: true }).press("Enter");
        await this.waitForNumberOfSeconds(1);

        const avatarElement = await this.page.waitForSelector("#user-info");
        const textContent = await avatarElement.innerText();

        expect(textContent).toContain("Ad");
    }
    async importExcelPage() {
        await this.page.goto("http://localhost:3000/exchangedata/importexcel");
    }
    async projectCreatePage() {
        await this.page.goto("http://localhost:3000/project/create");
    }
}
