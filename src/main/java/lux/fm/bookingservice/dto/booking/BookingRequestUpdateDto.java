package lux.fm.bookingservice.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import java.time.LocalDate;
import lux.fm.bookingservice.validation.BookingDatesValid;

@BookingDatesValid(field = {"checkIn", "checkOut"})
public record BookingRequestUpdateDto(
        @FutureOrPresent
        LocalDate checkIn,
        @Future
        LocalDate checkOut) {
}
