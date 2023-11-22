package lux.fm.bookingservice.service.impl;

import static lux.fm.bookingservice.model.Booking.Status;

import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.dto.payment.CreatePaymentRequestDto;
import lux.fm.bookingservice.dto.payment.PaymentResponseDto;
import lux.fm.bookingservice.dto.payment.PaymentSessionDto;
import lux.fm.bookingservice.exception.PaymentException;
import lux.fm.bookingservice.mapper.PaymentMapper;
import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Payment;
import lux.fm.bookingservice.repository.BookingRepository;
import lux.fm.bookingservice.repository.PaymentRepository;
import lux.fm.bookingservice.service.PaymentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;

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
            throw new PaymentException(
                    "You already have payment with booking id %d. Cancel it or pay"
                            .formatted(booking.getId()));
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
    public BookingResponseDto success(String sessionId) {
        return null;
    }

    @Override
    public PaymentResponseDto cancel(String sessionId) {
        return null;
    }

    @Override
    public List<PaymentResponseDto> findByUser(Long userId) {
        return null;
    }
}