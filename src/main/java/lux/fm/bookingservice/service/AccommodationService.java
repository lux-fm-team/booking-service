package lux.fm.bookingservice.service;

import java.util.List;
import lux.fm.bookingservice.dto.accommodation.AccommodationDto;
import lux.fm.bookingservice.dto.accommodation.CreateAccommodationRequestDto;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {

    AccommodationDto save(CreateAccommodationRequestDto accommodationRequestDto);

    List<AccommodationDto> findAll(Pageable pageable);

    AccommodationDto findById(Long id);

    AccommodationDto updateById(Long id, CreateAccommodationRequestDto
            accommodationRequestDto);

    void deleteById(Long id);
}
