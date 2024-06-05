import i18next from "i18next";
import { initReactI18next } from "react-i18next";
import "@testing-library/jest-dom/vitest";

import en from "./assets/languages/en.json";
import nl from "./assets/languages/nl.json";

// Configure i18next to use the ci locale, causing the text to be the same as the key
i18next.use(initReactI18next).init({
    lng: "ci-mode",
    resources: {
        en,
        nl,
    },
});
