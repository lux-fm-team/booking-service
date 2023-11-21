package lux.fm.bookingservice.dto.accommodation;

import java.math.BigDecimal;
import java.util.List;
import lux.fm.bookingservice.model.AccommodationType;

public record AccommodationDto(
        Long id,
        AccommodationType type,
        String location,
        String size,
        List<String> amenities,
        BigDecimal dailyRate,
        Integer availability
) {
}
