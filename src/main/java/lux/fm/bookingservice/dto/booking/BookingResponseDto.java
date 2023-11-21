package lux.fm.bookingservice.dto.booking;

import java.time.LocalDate;
import lux.fm.bookingservice.model.Booking.Status;

public record BookingResponseDto(
        Long id,
        Status status,
        Long accommodationId,
        LocalDate checkIn,
        LocalDate checkOut
) {
}
