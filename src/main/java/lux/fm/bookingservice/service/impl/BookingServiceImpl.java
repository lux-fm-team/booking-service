package lux.fm.bookingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.booking.BookingRequestCreateDto;
import lux.fm.bookingservice.dto.booking.BookingRequestUpdateDto;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.exception.BookingException;
import lux.fm.bookingservice.mapper.BookingMapper;
import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Booking.Status;
import lux.fm.bookingservice.model.Payment;
import lux.fm.bookingservice.model.User;
import lux.fm.bookingservice.repository.AccommodationRepository;
import lux.fm.bookingservice.repository.BookingRepository;
import lux.fm.bookingservice.repository.PaymentRepository;
import lux.fm.bookingservice.service.BookingService;
import lux.fm.bookingservice.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final AccommodationRepository accommodationRepository;
    private final NotificationService notificationService;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public BookingResponseDto addBooking(
            Authentication authentication,
            BookingRequestCreateDto request
    ) {
        validateUnpaidBookings(authentication);

        Accommodation accommodation = getAccommodation(request.accommodationId());
        User user = (User) authentication.getPrincipal();

        validateAvailablePlaces(accommodation, request.checkIn(), request.checkOut());

        Booking booking = bookingMapper.toModel(request);
        booking.setUser(user);
        booking.setAccommodation(accommodation);

        if (user.getTelegramId() != null) {
            String message = "Booking was created:\n" + createNotification(booking);
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

    @Transactional
    @Override
    public BookingResponseDto updateBookingById(
            String username, BookingRequestUpdateDto requestUpdateDto, Long id) {
        Booking booking = bookingRepository.findBookingByUserEmailAndId(username, id)
                .orElseThrow(() -> new EntityNotFoundException(
                                "Booking with such id doesn't exist: " + id
                        )
                );
        validateAvailablePlaces(
                booking.getAccommodation(),
                requestUpdateDto.checkIn(),
                requestUpdateDto.checkOut()
        );
        bookingMapper.update(requestUpdateDto, booking);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public void deleteBookingById(Authentication authentication, Long id) {
        Booking booking = bookingRepository.findBookingByUserEmailAndId(
                        authentication.getName(), id)
                .orElseThrow(() -> new EntityNotFoundException(
                                "Booking with such id doesn't exist: " + id
                        )
                );
        User user = (User) authentication.getPrincipal();

        if (user.getTelegramId() != null) {
            String message = "Booking was deleted:\n" + createNotification(booking);
            notificationService.notifyUser(user.getTelegramId(), message);
        }

        bookingRepository.delete(booking);
    }

    private Accommodation getAccommodation(Long id) {
        return accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Such accommodation doesn't exist with id: " + id)
        );
    }

    private void validateAvailablePlaces(
            Accommodation accommodation,
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {
        Long count = bookingRepository.countBookingsInDateRange(
                accommodation.getId(), checkInDate, checkOutDate
        );
        if (count >= accommodation.getAvailability()) {
            throw new BookingException(
                    "The accommodation isn't available with id: " + accommodation.getId()
            );
        }
    }


    private void validateUnpaidBookings(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (paymentRepository
                .existsPaymentByBookingUserAndStatus(user, Payment.Status.PENDING)) {
            throw new BookingException("You have a payment with status PENDING");
        }
    }

    private String createNotification(Booking booking) {
        Accommodation accommodation = booking.getAccommodation();
        return """
                Location: %s
                Type: %s
                Check in date: %s
                Check out date: %s
                Total: %.2f
                """.formatted(
                accommodation.getLocation(),
                String.valueOf(accommodation.getType()),
                booking.getCheckIn(),
                booking.getCheckOut(),
                getTotal(booking)
        );
    }

    private BigDecimal getTotal(Booking booking) {
        return booking.getAccommodation().getDailyRate()
                .multiply(BigDecimal.valueOf(
                        ChronoUnit.DAYS.between(
                                booking.getCheckIn(), booking.getCheckOut()
                        )
                ));
    }
}
