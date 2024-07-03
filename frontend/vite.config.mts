import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";
import path from "path";

export default defineConfig(({ mode }) => {
    const rootPath = path.resolve(__dirname, "..");
    process.env = { ...process.env, ...loadEnv(mode, rootPath) };

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
        plugins: [react()],
        optimizeDeps: {
            include: ["@emotion/react", "@emotion/styled", "@mui/material/Tooltip"],
        },
        define: {
            VITE_REACT_APP_GIT_SHA: JSON.stringify(process.env.VITE_REACT_APP_GIT_SHA),
            VITE_REACT_APP_VERSION_NUMBER: JSON.stringify(process.env.VITE_REACT_APP_VERSION_NUMBER),
        },
    };
});
