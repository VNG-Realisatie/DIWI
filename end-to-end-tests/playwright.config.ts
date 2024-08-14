import { defineConfig, devices } from "@playwright/test";

/**
 * Read environment variables from file.
 * https://github.com/motdotla/dotenv
 */
// require('dotenv').config();

/**
 * See https://playwright.dev/docs/test-configuration.
 */
export default defineConfig({
    testDir: "./tests",
    /* Run tests in files in parallel */
    fullyParallel: true,
    /* Fail the build on CI if you accidentally left test.only in the source code. */
    forbidOnly: !!process.env.CI,
    /* Retry on CI only */
    retries: process.env.CI ? 2 : 0,
    /* Opt out of parallel tests on CI. */
    workers: process.env.CI ? 1 : undefined,
    /* Reporter to use. See https://playwright.dev/docs/test-reporters */
    reporter: [["html"], ["junit", { outputFile: "results.xml" }]],
    /* Shared settings for all the projects below. See https://playwright.dev/docs/api/class-testoptions. */
    use: {
        /* Base URL to use in actions like `await page.goto('/')`. */
        baseURL: "http://127.0.0.1:3000",

        /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
        trace: "on-first-retry",
    },

    /* Configure projects for major browsers */
    projects: [
        {
            name: "setup user",
            testMatch: /global\.setup\.ts/,
        },
        {
            name: "setup district",
            testMatch: /district\.setup\.ts/,
            dependencies: ["setup user"], // Ensure this runs after "setup user"
        },
        {
            name: "setup municipality",
            testMatch: /municipality\.setup\.ts/,
            dependencies: ["setup user"], // Ensure this runs after "setup user"
        },
        {
            name: "setup neighborhood",
            testMatch: /neighborhood\.setup\.ts/,
            dependencies: ["setup user"], // Ensure this runs after "setup user"
        },
        {
            name: "chromium",
            use: { ...devices["Desktop Chrome"] },
            dependencies: ["setup user", "setup district", "setup municipality", "setup neighborhood"],
        },
    ],
});
