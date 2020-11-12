package kz.danke.edge.service.exception;

import org.springframework.security.core.AuthenticationException;

public class EmptyLoginRequestBodyException extends AuthenticationException {

    public EmptyLoginRequestBodyException(String message) {
        super(message);
    }
}
