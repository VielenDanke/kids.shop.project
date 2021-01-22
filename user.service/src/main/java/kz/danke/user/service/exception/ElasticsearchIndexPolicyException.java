package kz.danke.user.service.exception;

public class ElasticsearchIndexPolicyException extends RuntimeException {
    public ElasticsearchIndexPolicyException(String message) {
        super(message);
    }
}
