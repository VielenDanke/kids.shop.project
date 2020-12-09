package kz.danke.kids.shop.exceptions;

public class ParamNotFoundException extends RuntimeException {

    public ParamNotFoundException() {
        super();
    }

    public ParamNotFoundException(String message) {
        super(message);
    }

    public ParamNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
