package kz.danke.user.service.exception;

public class UserNotAuthorizedException extends RuntimeException {

    public UserNotAuthorizedException() {
        super();
    }

    public UserNotAuthorizedException(String message) {
        super(message);
    }

    public UserNotAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
