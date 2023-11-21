package lux.fm.bookingservice.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lux.fm.bookingservice.validation.BookingDatesValid;
import org.springframework.format.annotation.DateTimeFormat;

@BookingDatesValid(field = {"checkIn", "checkOut"})
public record BookingRequestCreateDto(
        @Positive
        @NotNull
        Long accommodationId,

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @FutureOrPresent
        LocalDate checkIn,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Future
        LocalDate checkOut
) {
}
