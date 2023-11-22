package lux.fm.bookingservice.dto.payment;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreatePaymentRequestDto(
        @Positive
        BigDecimal price) {
}
