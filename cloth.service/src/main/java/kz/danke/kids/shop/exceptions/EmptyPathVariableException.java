package kz.danke.kids.shop.exceptions;

public class EmptyPathVariableException extends RuntimeException {

    public EmptyPathVariableException() {
        super();
    }

    public EmptyPathVariableException(String message) {
        super(message);
    }

    public EmptyPathVariableException(String message, Throwable cause) {
        super(message, cause);
    }
}
