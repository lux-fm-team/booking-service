package lux.fm.bookingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.accommodation.AccommodationDto;
import lux.fm.bookingservice.dto.accommodation.CreateAccommodationRequestDto;
import lux.fm.bookingservice.mapper.AccommodationMapper;
import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.repository.AccommodationRepository;
import lux.fm.bookingservice.service.AccommodationService;
import lux.fm.bookingservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    @Qualifier("telegramNotificationService")
    private final NotificationService telegramNotificationService;

    @Override
    public AccommodationDto save(CreateAccommodationRequestDto accommodationRequestDto) {
        Accommodation accommodation = accommodationMapper.toAccommodation(accommodationRequestDto);
        telegramNotificationService.notifyAboutCreatedAccommodation(accommodation);
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
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
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("No accommodation with id " + id));
        accommodationMapper.updateAccommodation(accommodationRequestDto, accommodation);
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public void deleteById(Long id) {
        checkAccommodationExistById(id);
        accommodationRepository.deleteById(id);
    }

    private void checkAccommodationExistById(Long id) {
        if (!accommodationRepository.existsById(id)) {
            throw new EntityNotFoundException("Accommodation doesn't exist with id " + id);
        }
    }
}
