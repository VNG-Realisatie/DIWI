import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
    base: "/",
    server: {
        host: "0.0.0.0",
        port: 3000,
        strictPort: true,
        proxy: {
            "^/rest/.*": "http://localhost:8080/",
        },
    },
    assetsInclude: "**/*.xlsx",
    plugins: [react()],
    test: {
        globals: true,
        environment: "jsdom",
        setupFiles: "./src/setupTests.ts",
        css: true,
        reporters: ["verbose"],
        coverage: {
            reporter: ["html"],
            include: ["src/**/*"],
            exclude: ["src/i18n.tsx", "src/index.tsx", "src/reportWebVitals.ts"],
        },
    },
});
