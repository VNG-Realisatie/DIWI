import { test, expect } from "@playwright/test";

test("test", async ({ page }) => {
    await page.goto("http://localhost:3000/rest/auth/login");

    await page.getByLabel("Username or email").click();
    await page.getByLabel("Username or email").fill("admin");
    await page.getByLabel("Username or email").press("Enter");
    await page.getByLabel("Password").click();
    await page.getByLabel("Password").fill("admin");
    await page.getByLabel("Password").press("Enter");

    await expect(page).toHaveURL("http://localhost:3000");
});
