package kz.danke.edge.service.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenNotValidException extends AuthenticationException {

    public TokenNotValidException(String message) {
        super(message);
    }
}
