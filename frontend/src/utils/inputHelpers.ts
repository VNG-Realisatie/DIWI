const decimalSeparator = ",";
const thousandSeparator = ".";

function formatNumberWithSeparators(value: number): string {
    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, thousandSeparator);
}

export function formatMonetaryValue(val: number | null): string {
    if (val === null) {
        return "";
    } else {
        const euros = Math.floor(val / 100);
        const cents = val % 100;
        return `${formatNumberWithSeparators(euros)}${decimalSeparator}${cents.toString().padStart(2, "0")}`;
    }
}
