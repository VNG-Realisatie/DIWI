import { TextEncoder, TextDecoder } from "util";
import "@testing-library/jest-dom";

Object.assign(global, { TextDecoder, TextEncoder });

// Resize observer isn't implemented by default in jest. Make a fake here.
window.ResizeObserver = class ResizeObserver {
    observe() {}
    disconnect() {}
};
