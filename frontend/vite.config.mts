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
    optimizeDeps: {
        include: [
            '@emotion/react',
            '@emotion/styled',
            '@mui/material/Tooltip'
          ],
    }
});
