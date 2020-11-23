package kz.danke.kids.shop.exceptions;

public class EmptyRequestException extends RuntimeException {

    public EmptyRequestException() {
    }

    public EmptyRequestException(String message) {
        super(message);
    }
}
