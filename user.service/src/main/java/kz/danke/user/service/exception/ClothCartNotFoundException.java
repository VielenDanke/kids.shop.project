package kz.danke.user.service.exception;

public class ClothCartNotFoundException extends RuntimeException {

    private final int responseStatus;

    public ClothCartNotFoundException(int responseStatus) {
        super();
        this.responseStatus = responseStatus;
    }

    public ClothCartNotFoundException(String message, int responseStatus) {
        super(message);
        this.responseStatus = responseStatus;
    }

    public ClothCartNotFoundException(String message, Throwable cause, int responseStatus) {
        super(message, cause);
        this.responseStatus = responseStatus;
    }

    public int getResponseStatus() {
        return responseStatus;
    }
}
