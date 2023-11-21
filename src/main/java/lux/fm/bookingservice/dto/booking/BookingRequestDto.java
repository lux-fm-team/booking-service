package lux.fm.bookingservice.dto.booking;

import java.time.LocalDate;

public record BookingRequestDto(
        Long accommodationId,
        LocalDate checkIn,
        LocalDate checkOut
) {
}
