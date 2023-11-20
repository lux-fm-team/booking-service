package lux.fm.bookingservice.service;

import java.util.List;
import lux.fm.bookingservice.dto.accommodation.AccommodationDto;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {
    List<AccommodationDto> findAll(Pageable pageable);

    AccommodationDto findById(Long id);
}
