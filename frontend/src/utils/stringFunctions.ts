export function capitalizeFirstLetters(text: string, coerceLower: boolean = true): string {
    if (coerceLower) {
        return text
            .split(" ")
            .map((word) => word[0].toUpperCase() + word.substring(1).toLowerCase())
            .join(" ");
    } else {
        return text
            .split(" ")
            .map((word) => word[0].toUpperCase() + word.substring(1))
            .join(" ");
    }
}
