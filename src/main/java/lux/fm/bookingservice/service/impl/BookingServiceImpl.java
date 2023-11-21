package lux.fm.bookingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.booking.BookingRequestCreateDto;
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
import lux.fm.bookingservice.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public BookingResponseDto addBooking(
            Authentication authentication,
            BookingRequestCreateDto request
    ) {
        validateAvailablePlaces(request.accommodationId(), request.checkIn(), request.checkOut());
        Accommodation accommodation = getAccommodation(request.accommodationId());
        Booking booking = bookingMapper.toModel(request);
        booking.setUser((User) authentication.getPrincipal());
        booking.setAccommodation(accommodation);
        User user = getCurrentlyAuthenticatedUser();
        if (user.getTelegramId() != null) {
            String message = "Booking was deleted: " + bookingMapper.toDto(booking).toString();
            notificationService.notifyUser(user.getTelegramId(), message);
        }
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponseDto> findBookingsByUserIdAndStatus(Long userId, Status status) {
        return bookingRepository.findByUserIdAndStatus(userId, status).stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> findUserBookings(String username) {
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
    public BookingResponseDto updateBookingById(
            String username, BookingRequestUpdateDto requestUpdateDto, Long id) {
        Booking booking = bookingRepository.findBookingByUserEmailAndId(username, id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Booking with such id doesn't exist: " + id
                )
        );
        validateAvailablePlaces(
                booking.getAccommodation().getId(),
                requestUpdateDto.checkIn(),
                requestUpdateDto.checkOut()
        );
        bookingMapper.update(requestUpdateDto, booking);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public void deleteBookingById(String username, Long id) {
        Booking booking = bookingRepository.findBookingByUserEmailAndId(username, id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Booking with such id doesn't exist: " + id
                )
        );
        User user = getCurrentlyAuthenticatedUser();
        if (user.getTelegramId() != null) {
            String message = "Booking was deleted: " + bookingMapper.toDto(booking).toString();
            notificationService.notifyUser(user.getTelegramId(), message);
        }
        bookingRepository.delete(booking);
    }


    private User getCurrentlyAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username).get();
    }

    private Accommodation getAccommodation(Long id) {
        return accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Such accommodation doesn't exist with id: " + id)
        );
    }

    private void validateAvailablePlaces(
            Long accommodationId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Accommodation doesn't exist with id: " + accommodationId
                ));
        Long count = bookingRepository.countBookingsInDate(
                accommodation.getId(), checkInDate, checkOutDate
        );
        if (count >= accommodation.getAvailability()) {
            throw new BookingException(
                    "The accommodation isn't available with id: " + accommodationId
            );
        }
    }
}
