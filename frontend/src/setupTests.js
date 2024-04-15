import "@testing-library/jest-dom";
import { TextDecoder, TextEncoder } from "util";

import i18next from "i18next";
import { initReactI18next } from "react-i18next";

import en from "./assets/languages/en.json";
import nl from "./assets/languages/nl.json";
Object.assign(global, { TextDecoder, TextEncoder });

// Resize observer isn't implemented by default in jest. Make a fake here.
window.ResizeObserver = class ResizeObserver {
    observe() {}
    disconnect() {}
};

// Configure i18next to use the ci locale, causing the text to be the same as the key
i18next.use(initReactI18next).init({
    lng: "cimode",
    resources: {
        en,
        nl,
    },
});
