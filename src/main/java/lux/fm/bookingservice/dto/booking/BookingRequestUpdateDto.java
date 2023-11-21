package lux.fm.bookingservice.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import java.time.LocalDate;
import lux.fm.bookingservice.validation.BookingDatesValid;
import org.springframework.format.annotation.DateTimeFormat;

@BookingDatesValid(field = {"checkIn", "checkOut"})
public record BookingRequestUpdateDto(
        @FutureOrPresent
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate checkIn,
        @Future
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate checkOut) {
}
