package nl.vng.diwi.rest;

public class InvalidConfigException extends VngServerErrorException {

    public InvalidConfigException(String message) {
        super(message);
    }

}
