package lux.fm.bookingservice.dto.payment;

import static lux.fm.bookingservice.model.Payment.Status;

import java.math.BigDecimal;

public record PaymentResponseDto(
        Long bookingId,
        Status status,
        String sessionUrl,
        BigDecimal amountToPay) {
}
