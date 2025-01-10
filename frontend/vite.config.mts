import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";
import path from "path";
import fs from "fs";
import checker from "vite-plugin-checker";

export default defineConfig(({ mode }) => {
    const rootPath = path.resolve(__dirname, "..");
    process.env = { ...process.env, ...loadEnv(mode, rootPath) };

    const versionEnvPath = path.resolve(__dirname, "../version.env");
    if (fs.existsSync(versionEnvPath)) {
        const envConfig = fs
            .readFileSync(versionEnvPath, "utf-8")
            .split("\n")
            .filter(Boolean)
            .reduce(
                (acc, line) => {
                    const [key, value] = line.split("=");
                    acc[key] = value;
                    return acc;
                },
                {} as Record<string, string>,
            );

        process.env = { ...process.env, ...envConfig };
    }

    return {
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
        plugins: [
            react(),
            checker({
                typescript: true,
                eslint: {
                    lintCommand: 'eslint "./src/**/*.{ts,tsx}"',
                },
            }),
        ],
        optimizeDeps: {
            include: ["@emotion/react", "@emotion/styled", "@mui/material/Tooltip"],
        },
        define: {
            "import.meta.env.VITE_REACT_APP_GIT_SHA": JSON.stringify(process.env.VITE_REACT_APP_GIT_SHA),
            "import.meta.env.VITE_REACT_APP_VERSION_NUMBER": JSON.stringify(process.env.VITE_REACT_APP_VERSION_NUMBER),
        },
        build: {
            rollupOptions: {
                external: ["@esri/arcgis-rest-request"],
            },
        },
    };
});
