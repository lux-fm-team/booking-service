package lux.fm.bookingservice.service;

import java.util.List;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.dto.payment.CreatePaymentRequestDto;
import lux.fm.bookingservice.dto.payment.PaymentResponseDto;
import org.springframework.security.core.Authentication;

public interface PaymentService {
    PaymentResponseDto createPayment(
            Authentication authentication, CreatePaymentRequestDto requestDto
    );

    BookingResponseDto success(String sessionId);

    PaymentResponseDto cancel(String sessionId);

    List<PaymentResponseDto> findByUser(Long userId);
}
