package lux.fm.bookingservice.dto.accommodation;

import java.math.BigDecimal;
import java.util.List;
import lux.fm.bookingservice.model.Accommodation;

public record AccommodationDto(
        Long id,
        Accommodation.AccommodationType type,
        String location,
        String size,
        List<String> amenities,
        BigDecimal dailyRate,
        Integer availability
) {
}
