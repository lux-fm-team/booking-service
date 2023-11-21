package lux.fm.bookingservice.exception;

public class BookingInvalidDateException extends RuntimeException {
    public BookingInvalidDateException() {
        super();
    }

    public BookingInvalidDateException(String message) {
        super(message);
    }
}
