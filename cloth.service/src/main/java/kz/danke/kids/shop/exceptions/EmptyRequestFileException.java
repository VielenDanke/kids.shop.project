package kz.danke.kids.shop.exceptions;

public class EmptyRequestFileException extends RuntimeException {

    public EmptyRequestFileException() {
    }

    public EmptyRequestFileException(String message) {
        super(message);
    }
}
