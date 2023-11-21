package lux.fm.bookingservice.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.mapper.BookingMapper;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Status;
import lux.fm.bookingservice.repository.booking.BookingRepository;
import lux.fm.bookingservice.service.BookingService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public List<BookingResponseDto> findBookingsByUserIdAndStatus(
            Long userId,
            Status status) {
        return bookingRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> findMyBookings(String username) {
        return bookingRepository.findBookingsByUserEmail(username)
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public BookingResponseDto findBookingById(String username, Long id) {
        List<Booking> bookings =
                bookingRepository.findBookingsByUserEmail(username);
        return bookings.stream()
                .filter(e -> e.getId().equals(id))
                .map(bookingMapper::toDto)
                .findFirst()
                .get();
    }
}
