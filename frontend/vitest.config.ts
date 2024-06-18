// vitest.config.ts
import { defineConfig } from "vitest/config";

export default defineConfig({
    test: {
        globals: true,
        environment: "jsdom",
        setupFiles: ["./src/setupTests.js"],
        css: true,
        reporters: ["verbose"],
        coverage: {
            reporter: ["html"],
            include: ["src/**/*"],
            exclude: ["src/i18n.tsx", "src/index.tsx", "src/reportWebVitals.ts"],
        },
    },
});
