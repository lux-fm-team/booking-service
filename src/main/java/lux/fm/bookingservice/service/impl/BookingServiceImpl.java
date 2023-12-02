package lux.fm.bookingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
import lux.fm.bookingservice.model.Booking.Status;
import lux.fm.bookingservice.model.Payment;
import lux.fm.bookingservice.model.User;
import lux.fm.bookingservice.repository.AccommodationRepository;
import lux.fm.bookingservice.repository.BookingRepository;
import lux.fm.bookingservice.repository.PaymentRepository;
import lux.fm.bookingservice.repository.UserRepository;
import lux.fm.bookingservice.service.BookingService;
import lux.fm.bookingservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private static final int MAXIMUM_USER_BOOKINGS_CAPACITY = 5;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final AccommodationRepository accommodationRepository;
    @Qualifier("telegramNotificationService")
    private final NotificationService telegramNotificationService;
    @Qualifier("emailNotificationService")
    private final NotificationService emailNotificationService;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final StripeService stripeService;

    @Override
    @Transactional
    public BookingResponseDto addBooking(
            Authentication authentication,
            BookingRequestCreateDto request
    ) {
        validateUnpaidBookings(authentication);

        Accommodation accommodation = getAccommodation(request.accommodationId());
        User user = userRepository.findByEmail(authentication.getName()).get();

        validateAvailablePlaces(accommodation, request.checkIn(), request.checkOut());

        Booking booking = bookingMapper.toModel(request);
        booking.setUser(user);
        booking.setAccommodation(accommodation);
        emailNotificationService.notifyAboutCreatedBooking(user, booking);
        telegramNotificationService.notifyAboutCreatedBooking(user, booking);
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
        Booking booking = bookingRepository.findByUserEmailAndId(username, id).orElseThrow(
                () -> new EntityNotFoundException("There's no booking with id: " + id)
        );
        return bookingMapper.toDto(booking);
    }

    @Transactional
    @Override
    public BookingResponseDto updateBookingById(
            String username, BookingRequestUpdateDto requestUpdateDto, Long id) {
        Booking booking = bookingRepository.findByUserEmailAndId(username, id)
                .orElseThrow(() -> new EntityNotFoundException(
                                "Booking with such id doesn't exist: " + id
                        )
                );
        if (booking.getPayment() != null
                && booking.getPayment().getStatus().equals(Payment.Status.PENDING)) {
            throw new BookingException(
                    "Can't update booking until payment is completed");
        }

        validateAvailablePlaces(
                booking.getAccommodation(),
                requestUpdateDto.checkIn(),
                requestUpdateDto.checkOut()
        );
        bookingMapper.update(requestUpdateDto, booking);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public void deleteBookingById(Authentication authentication, Long id) {
        Booking booking = bookingRepository.findByUserEmailAndId(
                        authentication.getName(), id)
                .orElseThrow(() -> new EntityNotFoundException(
                                "Booking with such id doesn't exist: " + id
                        )
                );

        if (booking.getStatus() != Status.PENDING) {
            throw new BookingException("Booking can't be deleted on this stage");
        }

        if (booking.getPayment() != null) {
            stripeService.expireSession(booking.getPayment().getSessionId());
        }

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException("No user with such email"
                        + authentication.getName())
        );
        if (bookingRepository.countAllByUserEmail(authentication.getName())
                >= MAXIMUM_USER_BOOKINGS_CAPACITY
        ) {
            throw new BookingException("User can't have more than 5 bookings at a time");
        }
        emailNotificationService.notifyAboutCanceledBooking(user, booking);
        telegramNotificationService.notifyAboutCanceledBooking(user, booking);
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
        if (paymentRepository
                .existsPaymentByBookingUserEmailAndStatus(authentication.getName(),
                        Payment.Status.PENDING)) {
            throw new BookingException("You have a payment with status PENDING");
        }
    }
}
