package lux.fm.bookingservice.service;

import java.util.List;
import lux.fm.bookingservice.dto.booking.BookingRequestCreateDto;
import lux.fm.bookingservice.dto.booking.BookingRequestUpdateDto;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.model.Status;
import org.springframework.security.core.Authentication;

public interface BookingService {
    List<BookingResponseDto> findBookingsByUserIdAndStatus(Long id, Status status);

    List<BookingResponseDto> findUserBookings(String token);

    BookingResponseDto findBookingById(String token, Long id);

    BookingResponseDto updateBookingById(
            String username, BookingRequestUpdateDto requestUpdateDto, Long id);

    void deleteBookingById(String username, Long id);

    BookingResponseDto addBooking(Authentication authentication, BookingRequestCreateDto request);
}

