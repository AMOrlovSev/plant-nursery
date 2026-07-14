package sev.amorlov.plant_nursery.exception;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}