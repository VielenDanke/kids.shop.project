package kz.danke.kids.shop.exceptions;

public class ClothNotEnoughAmountException  extends RuntimeException{

    public ClothNotEnoughAmountException() {
        super();
    }

    public ClothNotEnoughAmountException(String message) {
        super(message);
    }

    public ClothNotEnoughAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
