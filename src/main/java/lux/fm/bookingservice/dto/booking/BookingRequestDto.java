package lux.fm.bookingservice.dto.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record BookingRequestDto(
        @Positive
        @NotNull
        Long accommodationId,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate checkIn,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate checkOut
) {
}
