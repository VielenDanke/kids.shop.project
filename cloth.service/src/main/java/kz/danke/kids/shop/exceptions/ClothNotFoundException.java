package kz.danke.kids.shop.exceptions;

public class ClothNotFoundException extends RuntimeException {

    public ClothNotFoundException(String message) {
        super(message);
    }

    public ClothNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
