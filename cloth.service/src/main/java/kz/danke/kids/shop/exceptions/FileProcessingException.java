package kz.danke.kids.shop.exceptions;

public class FileProcessingException extends RuntimeException {
    public FileProcessingException(Exception e) {
        super(e);
    }
}
