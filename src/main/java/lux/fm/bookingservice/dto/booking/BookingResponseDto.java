package lux.fm.bookingservice.dto.booking;

import java.time.LocalDate;
import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.model.Status;

public record BookingResponseDto(
        Status status,
        Accommodation accommodation,
        LocalDate checkIn,
        LocalDate checkOut
) {
}
