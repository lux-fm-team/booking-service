package lux.fm.bookingservice.service;

import java.util.List;
import lux.fm.bookingservice.dto.booking.BookingRequestUpdateDto;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.model.Status;

public interface BookingService {
    List<BookingResponseDto> findBookingsByUserIdAndStatus(Long id, Status status);

    List<BookingResponseDto> findMyBookings(String token);

    BookingResponseDto findBookingById(String token, Long id);

    BookingResponseDto updateBookById(BookingRequestUpdateDto requestUpdateDto, Long id);
}

