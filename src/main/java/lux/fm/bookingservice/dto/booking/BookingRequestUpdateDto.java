package lux.fm.bookingservice.dto.booking;

import java.time.LocalDate;

public record BookingRequestUpdateDto(LocalDate checkIn,
                                      LocalDate checkOut) {
}
