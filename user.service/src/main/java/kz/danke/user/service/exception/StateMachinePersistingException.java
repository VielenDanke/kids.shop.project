package kz.danke.user.service.exception;

public class StateMachinePersistingException extends RuntimeException {

    public StateMachinePersistingException() {
        super();
    }

    public StateMachinePersistingException(String message) {
        super(message);
    }

    public StateMachinePersistingException(String message, Throwable cause) {
        super(message, cause);
    }
}
