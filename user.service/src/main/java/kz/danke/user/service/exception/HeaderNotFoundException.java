package kz.danke.user.service.exception;

public class HeaderNotFoundException extends RuntimeException {

    public HeaderNotFoundException() {
        super();
    }

    public HeaderNotFoundException(String message) {
        super(message);
    }

    public HeaderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
