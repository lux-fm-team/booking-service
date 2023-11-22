package lux.fm.bookingservice.dto.payment;

import jakarta.validation.constraints.Positive;

public record CreatePaymentRequestDto(@Positive Long bookingId) {
}
