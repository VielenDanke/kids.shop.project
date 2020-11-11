package kz.danke.user.service.exception;

public class AuthorizationHeaderNotFoundException extends RuntimeException {

    public AuthorizationHeaderNotFoundException() {
        super();
    }

    public AuthorizationHeaderNotFoundException(String message) {
        super(message);
    }

    public AuthorizationHeaderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
