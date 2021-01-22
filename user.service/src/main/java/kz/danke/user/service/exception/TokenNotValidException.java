package kz.danke.user.service.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenNotValidException extends AuthenticationException {
    public TokenNotValidException(String msg, Throwable t) {
        super(msg, t);
    }

    public TokenNotValidException(String msg) {
        super(msg);
    }
}
