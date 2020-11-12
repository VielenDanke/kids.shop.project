package kz.danke.edge.service.exception;

import org.springframework.security.core.AuthenticationException;

public class ParseLoginRequestException extends AuthenticationException {

    public ParseLoginRequestException(String message) {
        super(message);
    }
}
