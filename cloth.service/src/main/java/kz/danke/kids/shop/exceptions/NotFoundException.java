package kz.danke.kids.shop.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String notFound) {
        super(notFound);
    }
}
