package lux.fm.bookingservice.service.impl;

import static lux.fm.bookingservice.model.Booking.Status;

import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.dto.payment.CreatePaymentRequestDto;
import lux.fm.bookingservice.dto.payment.PaymentResponseDto;
import lux.fm.bookingservice.dto.payment.PaymentSessionDto;
import lux.fm.bookingservice.dto.payment.PaymentWithoutSessionDto;
import lux.fm.bookingservice.exception.PaymentException;
import lux.fm.bookingservice.mapper.BookingMapper;
import lux.fm.bookingservice.mapper.PaymentMapper;
import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Payment;
import lux.fm.bookingservice.repository.BookingRepository;
import lux.fm.bookingservice.repository.PaymentRepository;
import lux.fm.bookingservice.service.NotificationService;
import lux.fm.bookingservice.service.PaymentService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMapper paymentMapper;
    private final BookingMapper bookingMapper;
    private final StripeService stripeService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PaymentResponseDto createPayment(
            Authentication authentication,
            CreatePaymentRequestDto requestDto,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        Booking booking = bookingRepository.findByUserEmailAndId(
                authentication.getName(), requestDto.bookingId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Booking with such id doesn't exist: " + requestDto.bookingId())
                );

        if (booking.getStatus() != Status.PENDING) {
            throw new PaymentException("You can't create payment for non-pending booking");
        }

        if (paymentRepository.existsByBooking(booking)) {
            throw new PaymentException("You already have payment with booking id %d"
                            .formatted(booking.getId())
            );
        }

        Accommodation accommodation = booking.getAccommodation();
        PaymentSessionDto paymentSessionDto = new PaymentSessionDto(
                booking.getTotal(),
                accommodation.getType() + ", " + accommodation.getLocation(),
                "Size %s, Amenities: %s".formatted(
                        accommodation.getSize(),
                        String.join(", ", accommodation.getAmenities())
                )
        );
        Session session = stripeService.createStripeSession(
                paymentSessionDto,
                uriComponentsBuilder
        );

        Payment payment = paymentMapper.toPayment(booking);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public PaymentWithoutSessionDto successPayment(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new PaymentException("Payment session not found"));
        if (!stripeService.isSessionPaid(sessionId)) {
            throw new PaymentException("Payment hasn't gone through yet. "
                    + "Follow the session and pay for your booking");
        }
        payment.setStatus(Payment.Status.PAID);
        payment.setSessionId(UUID.randomUUID().toString());
        Booking booking = payment.getBooking();
        booking.setStatus(Status.CONFIRMED);

        Long telegramId = booking.getUser().getTelegramId();
        if (telegramId != null) {
            notificationService.notifyAboutSuccessPayment(telegramId, payment);
        }

        return paymentMapper.toDtoWithoutSession(payment);
    }

    @Override
    @Transactional
    public BookingResponseDto cancelPayment(String sessionId) {
        Payment payment = paymentRepository.findBySessionIdAndStatus(
                sessionId, Payment.Status.PENDING)
                .orElseThrow(() -> new PaymentException("Payment session not found"));
        Booking booking = payment.getBooking();
        // @Todo: invalidate Stripe session
        paymentRepository.delete(payment);
        return bookingMapper.toDto(booking);
    }

    @Override
    public List<PaymentWithoutSessionDto> findByUser(Long userId, Pageable pageable) {
        return Optional.ofNullable(userId)
                .map((id) -> paymentRepository.findByUser(id, pageable))
                .orElseGet(() -> paymentRepository.findAll(pageable))
                .stream()
                .map(paymentMapper::toDtoWithoutSession)
                .toList();
    }
}
