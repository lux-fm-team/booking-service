package lux.fm.bookingservice.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;
import lux.fm.bookingservice.model.AccommodationType;

public record CreateAccommodationRequestDto(
        @NotBlank
        AccommodationType type,
        @NotBlank
        String location,
        @NotBlank
        String size,
        @NotBlank
        List<String> amenities,
        @NotNull
        @PositiveOrZero
        BigDecimal dailyRate,
        @NotNull
        Integer availability
) {
}
