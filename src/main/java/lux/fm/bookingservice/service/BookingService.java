package lux.fm.bookingservice.service;

import java.util.List;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.model.Status;

public interface BookingService {
    List<BookingResponseDto> findMyBookings(String token);
    List<BookingResponseDto> findBookingsByUserIdAndStatus(String token, Long id, Status status);
    BookingResponseDto findBookingById(String token, Long id);
}

