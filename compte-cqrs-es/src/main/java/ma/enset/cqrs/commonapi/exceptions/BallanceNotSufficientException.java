package ma.enset.cqrs.commonapi.exceptions;

public class BallanceNotSufficientException extends RuntimeException {
    public BallanceNotSufficientException(String s) {
        super(s);
    }
}
