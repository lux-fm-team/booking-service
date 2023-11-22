package lux.fm.bookingservice.dto.payment;

import static lux.fm.bookingservice.model.Payment.Status;

import java.math.BigDecimal;

public record PaymentWithoutSessionDto(
        Long bookingId,
        Status status,
        BigDecimal amountToPay) {
}
