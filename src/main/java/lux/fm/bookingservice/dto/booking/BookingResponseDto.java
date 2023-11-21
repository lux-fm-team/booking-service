package lux.fm.bookingservice.dto.booking;

import java.time.LocalDate;
import lux.fm.bookingservice.model.Status;

public record BookingResponseDto(
        Status status,
        Long accommodationId,
        LocalDate checkIn,
        LocalDate checkOut
) {
}
