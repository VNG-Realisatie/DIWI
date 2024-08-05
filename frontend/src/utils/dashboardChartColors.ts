export const chartColors = ["#0D3B66", "#145DA0", "#1E7AC9", "#2A9DF4", "#63B2F5", "#8CC6F5", "#B5DAF7", "#D6EAF8", "#E9F5FB", "#F7FBFD"];
export const grayScaleColors = ["#2f2f2f", "#3f3f3f", "#4f4f4f", "#5f5f5f", "#6f6f6f", "#7f7f7f", "#8f8f8f", "#9f9f9f", "#afafaf", "#bfbfbf"];

function hexToRgb(hex: string): { r: number; g: number; b: number } {
    const bigint = parseInt(hex.slice(1), 16);
    return {
        r: (bigint >> 16) & 255,
        g: (bigint >> 8) & 255,
        b: bigint & 255,
    };
}
export function generateColorsArray(n: number): string[] {
    const startColor = hexToRgb("#0D3B66"); // Dark Blue (Hex: #0D3B66)
    const endColor = hexToRgb("#D3D3D3"); // Light Gray (Hex: #D3D3D3)
    const interpolate = (start: number, end: number, factor: number) => {
        return Math.round(start + (end - start) * factor);
    };

    const colors = [];
    for (let i = 0; i < n; i++) {
        const factor = i / (n - 1);
        const r = interpolate(startColor.r, endColor.r, factor);
        const g = interpolate(startColor.g, endColor.g, factor);
        const b = interpolate(startColor.b, endColor.b, factor);
        colors.push(`rgb(${r}, ${g}, ${b})`);
    }

    return colors;
}
