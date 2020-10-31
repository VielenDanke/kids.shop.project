package kz.danke.edge.service.exception;

public class UserAlreadyExistsException extends RuntimeException{

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
