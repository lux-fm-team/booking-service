package lux.fm.bookingservice.exception;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}
