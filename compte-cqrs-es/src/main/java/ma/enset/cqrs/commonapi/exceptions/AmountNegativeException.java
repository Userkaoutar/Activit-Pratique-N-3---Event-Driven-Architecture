package ma.enset.cqrs.commonapi.exceptions;


public class AmountNegativeException extends RuntimeException{
    public AmountNegativeException(String message) {
        super(message);

    }
}
