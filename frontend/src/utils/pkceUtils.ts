export const generateCodeVerifier = (): string => {
    const array = new Uint32Array(43);
    window.crypto.getRandomValues(array);
    return Array.from(array, (num) => ("0" + (num % 36).toString(36)).slice(-1)).join("");
};

export const generateCodeChallenge = async (codeVerifier: string): Promise<string> => {
    const encoder = new TextEncoder();
    const data = encoder.encode(codeVerifier);
    const hash = await window.crypto.subtle.digest("SHA-256", data);
    return btoa(String.fromCharCode(...new Uint8Array(hash)))
        .replace(/\+/g, "-")
        .replace(/\//g, "_")
        .replace(/=+$/, "");
};
