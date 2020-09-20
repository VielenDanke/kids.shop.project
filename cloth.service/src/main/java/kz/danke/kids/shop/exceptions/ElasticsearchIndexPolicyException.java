package kz.danke.kids.shop.exceptions;

public class ElasticsearchIndexPolicyException extends RuntimeException {

    public ElasticsearchIndexPolicyException() {
        super();
    }

    public ElasticsearchIndexPolicyException(String message) {
        super(message);
    }
}
