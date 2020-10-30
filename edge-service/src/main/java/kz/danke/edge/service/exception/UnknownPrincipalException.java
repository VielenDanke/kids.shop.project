package kz.danke.edge.service.exception;

public class UnknownPrincipalException extends RuntimeException {
    public UnknownPrincipalException(String exMessage) {
        super(exMessage);
    }
}
