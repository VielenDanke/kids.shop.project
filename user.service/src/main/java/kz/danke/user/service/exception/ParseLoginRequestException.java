package kz.danke.user.service.exception;

import org.springframework.security.core.AuthenticationException;

public class ParseLoginRequestException extends AuthenticationException {

    public ParseLoginRequestException(String msg, Throwable t) {
        super(msg, t);
    }

    public ParseLoginRequestException(String msg) {
        super(msg);
    }
}
