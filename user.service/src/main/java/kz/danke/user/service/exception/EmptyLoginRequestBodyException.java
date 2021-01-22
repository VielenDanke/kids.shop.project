package kz.danke.user.service.exception;

import org.springframework.security.core.AuthenticationException;

public class EmptyLoginRequestBodyException extends AuthenticationException {

    public EmptyLoginRequestBodyException(String msg, Throwable t) {
        super(msg, t);
    }

    public EmptyLoginRequestBodyException(String msg) {
        super(msg);
    }
}
