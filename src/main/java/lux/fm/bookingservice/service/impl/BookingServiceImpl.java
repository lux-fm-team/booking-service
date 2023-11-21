package lux.fm.bookingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.mapper.BookingMapper;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Status;
import lux.fm.bookingservice.repository.booking.BookingRepository;
import lux.fm.bookingservice.service.BookingService;
import lux.fm.bookingservice.utils.AuthorizationUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final AuthorizationUtil authorizationUtil;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public List<BookingResponseDto> findBookingsByUserIdAndStatus(
            Long userId,
            Status status) {
        return bookingRepository.findAll()
                .stream()
                .filter(e -> e.getUser().getId().equals(userId) && e.getStatus().equals(status))
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> findMyBookings(String token) {
        return bookingRepository.findBookingsByUser(authorizationUtil.getUserByToken(token))
                .orElse(new ArrayList<>())
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public BookingResponseDto findBookingById(String token, Long id) {
        List<Booking> bookings =
                bookingRepository.findBookingsByUser(authorizationUtil.getUserByToken(token))
                .orElseThrow(
                        () -> new EntityNotFoundException("There's no bookings with id: " + id)
                );

        return bookings.stream()
                .filter(e -> e.getId().equals(id))
                .map(bookingMapper::toDto)
                .findFirst()
                .get();
    }
}
