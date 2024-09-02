const decimalSeparator = ",";
export function formatMonetaryValue(val: number | null) {
    if (val === null) {
        return "";
    } else {
        const euros = Math.floor(val / 100);
        const cents = val % 100;
        return `${euros}${decimalSeparator}${cents.toString().padStart(2, "0")}`;
    }
}
