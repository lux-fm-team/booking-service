package lux.fm.bookingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.booking.BookingRequestDto;
import lux.fm.bookingservice.dto.booking.BookingRequestUpdateDto;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.exception.BookingException;
import lux.fm.bookingservice.mapper.BookingMapper;
import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Status;
import lux.fm.bookingservice.model.User;
import lux.fm.bookingservice.repository.accommodation.AccommodationRepository;
import lux.fm.bookingservice.repository.booking.BookingRepository;
import lux.fm.bookingservice.repository.user.UserRepository;
import lux.fm.bookingservice.service.BookingService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;

    @Override
    public List<BookingResponseDto> findBookingsByUserIdAndStatus(Long userId, Status status) {
        return bookingRepository.findByUserIdAndStatus(userId, status).stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> findMyBookings(String username) {
        return bookingRepository.findBookingsByUserEmail(username).stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public BookingResponseDto findBookingById(String username, Long id) {
        Booking booking = bookingRepository.findBookingByUserEmailAndId(username, id).orElseThrow(
                () -> new EntityNotFoundException("There's no booking with id: " + id)
        );
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingResponseDto updateBookingById(BookingRequestUpdateDto requestUpdateDto, Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with such id doesn't exist: " + id)
        );
        bookingMapper.update(requestUpdateDto, booking);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public void deleteBookingById(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with such id doesn't exist: " + id)
        );
        bookingRepository.delete(booking);
    }

    @Override
    public BookingResponseDto addBooking(BookingRequestDto request) {
        validate(request);

        User user = getCurrentlyAuthenticatedUser();
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setAccommodation(getAccommodation(request.accommodationId()));
        booking.setCheckIn(request.checkIn());
        booking.setCheckOut(request.checkOut());
        booking.setStatus(Status.PENDING);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    private User getCurrentlyAuthenticatedUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(name).get();
    }

    private Accommodation getAccommodation(Long id) {
        return accommodationRepository.findById(id).orElseThrow(
                () -> new RuntimeException("No such accommodation")
        );
    }

    private void validate(BookingRequestDto request) {

        LocalDate today = LocalDate.now();

        if (request.checkIn().isBefore(today) || request.checkIn().isAfter(request.checkOut())) {
            throw new BookingException("Bad date request");
        }

        Accommodation accommodation = accommodationRepository.findById(request.accommodationId())
                .orElseThrow(() -> new EntityNotFoundException("No such accommodation")
                );

        Long count = bookingRepository.countBookingsInDate(
                accommodation.getId(),
                request.checkIn(),
                request.checkOut()
        );

        if (count >= accommodation.getAvailability()) {
            throw new BookingException("Not available accommodation");
        }
    }
}
