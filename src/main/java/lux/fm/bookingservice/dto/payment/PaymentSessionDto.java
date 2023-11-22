package lux.fm.bookingservice.dto.payment;

import java.math.BigDecimal;

public record PaymentSessionDto(BigDecimal price, String name, String description) {
}
