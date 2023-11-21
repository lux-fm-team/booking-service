package lux.fm.bookingservice.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import lux.fm.bookingservice.model.Accommodation;

public record CreateAccommodationRequestDto(
        @NotNull
        Accommodation.AccommodationType type,
        @NotBlank
        String location,
        @NotBlank
        String size,
        @NotNull
        List<@NotBlank String> amenities,
        @NotNull
        @Positive
        BigDecimal dailyRate,
        @NotNull
        Integer availability
) {
}
