package nl.vng.diwi.models.validation;

public class Validation {

    public static final String COLOR_REGEX = "^#[0-9a-fA-F]{6}$";

    public static boolean validateColor(String color) {
        return !color.matches(Validation.COLOR_REGEX);
    }


}
