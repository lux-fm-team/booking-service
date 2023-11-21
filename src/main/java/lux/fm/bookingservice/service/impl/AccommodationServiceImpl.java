package lux.fm.bookingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.accommodation.AccommodationDto;
import lux.fm.bookingservice.dto.accommodation.CreateAccommodationRequestDto;
import lux.fm.bookingservice.mapper.AccommodationMapper;
import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.repository.accommodation.AccommodationRepository;
import lux.fm.bookingservice.service.AccommodationService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;

    @Override
    public AccommodationDto save(CreateAccommodationRequestDto accommodationRequestDto) {
        return accommodationMapper.toDto(accommodationRepository.save(
                accommodationMapper.toAccommodation(accommodationRequestDto)));
    }

    @Override
    public List<AccommodationDto> findAll(Pageable pageable) {
        return accommodationRepository.findAll(pageable).stream()
                .map(accommodationMapper::toDto)
                .toList();
    }

    @Override
    public AccommodationDto findById(Long id) {
        return accommodationRepository.findById(id)
                .map(accommodationMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No accommodation with id " + id));
    }

    @Override
    public AccommodationDto updateById(
            Long id, CreateAccommodationRequestDto accommodationRequestDto) {
        findById(id);
        Accommodation accommodation = accommodationMapper.toAccommodation(
                accommodationRequestDto);
        accommodation.setId(id);
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public void deleteById(Long id) {
        accommodationRepository.deleteById(id);
    }
}
