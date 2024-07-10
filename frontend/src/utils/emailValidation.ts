import { isEmail } from "commons-validator-es";

export function validateEmail(email: string) {
    return isEmail(email);
}
