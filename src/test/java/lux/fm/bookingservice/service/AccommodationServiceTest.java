package lux.fm.bookingservice.service;

import static lux.fm.bookingservice.model.Accommodation.AccommodationType.APARTMENT;
import static lux.fm.bookingservice.model.Accommodation.AccommodationType.HOUSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lux.fm.bookingservice.dto.accommodation.AccommodationDto;
import lux.fm.bookingservice.dto.accommodation.CreateAccommodationRequestDto;
import lux.fm.bookingservice.mapper.AccommodationMapper;
import lux.fm.bookingservice.mapper.impl.AccommodationMapperImpl;
import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.repository.AccommodationRepository;
import lux.fm.bookingservice.service.impl.AccommodationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class AccommodationServiceTest {
    @Mock
    private AccommodationRepository accommodationRepository;
    @Spy
    private AccommodationMapper accommodationMapper = new AccommodationMapperImpl();
    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    @Test
    @DisplayName("Find accommodation by existent id")
    void findById_WithValidId_ShouldReturnValidAccommodationDto() {
        Long id = 1L;

        Accommodation accommodation = new Accommodation();
        accommodation.setId(id);
        accommodation.setType(HOUSE);
        accommodation.setLocation("Ukraine");
        accommodation.setSize("2 floors, 5 rooms");
        accommodation.setAmenities(new ArrayList<>(List.of("Wi-Fi",
                "Conditioner", "Parking", "Coffee maker")));
        accommodation.setDailyRate(BigDecimal.valueOf(500));
        accommodation.setAvailability(3);

        when(accommodationRepository.findById(id)).thenReturn(Optional.of(accommodation));

        AccommodationDto accommodationDto = accommodationService.findById(id);
        assertThat(accommodationDto)
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("type", HOUSE)
                .hasFieldOrPropertyWithValue("location", "Ukraine")
                .hasFieldOrPropertyWithValue("size", "2 floors, 5 rooms")
                .hasFieldOrPropertyWithValue("amenities", List.of(new String[]{"Wi-Fi",
                        "Conditioner", "Parking", "Coffee maker"}))
                .hasFieldOrPropertyWithValue("dailyRate", BigDecimal.valueOf(500))
                .hasFieldOrPropertyWithValue("availability", 3);
    }

    @Test
    @DisplayName("Find accommodation by invalid id")
    void findById_WithInvalidId_ShouldThrowException() {
        Long id = -1L;

        when(accommodationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationService.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No accommodation with id " + id);
    }

    @Test
    @DisplayName("Create new valid accommodation")
    void save_ValidCreateAccommodationRequestDto_ReturnAccommodationDto() {
        CreateAccommodationRequestDto requestDto = new CreateAccommodationRequestDto(
                HOUSE,
                "Ukraine",
                "2 floors, 5 rooms",
                List.of("Wi-Fi", "Conditioner", "Parking", "Coffee maker"),
                BigDecimal.valueOf(500),
                3
        );

        Accommodation accommodation = new Accommodation();
        accommodation.setType(requestDto.type());
        accommodation.setLocation(requestDto.location());
        accommodation.setSize(requestDto.size());
        accommodation.setAmenities(requestDto.amenities());
        accommodation.setDailyRate(requestDto.dailyRate());
        accommodation.setAvailability(requestDto.availability());

        when(accommodationRepository.save(any())).thenReturn(accommodation);

        AccommodationDto accommodationDto = accommodationService.save(requestDto);

        assertThat(accommodationDto)
                .hasFieldOrPropertyWithValue("type", HOUSE)
                .hasFieldOrPropertyWithValue("location", "Ukraine")
                .hasFieldOrPropertyWithValue("size", "2 floors, 5 rooms")
                .hasFieldOrPropertyWithValue("amenities", List.of("Wi-Fi",
                        "Conditioner", "Parking", "Coffee maker"))
                .hasFieldOrPropertyWithValue("dailyRate", BigDecimal.valueOf(500))
                .hasFieldOrPropertyWithValue("availability", 3);
    }

    @Test
    @DisplayName("Update accommodation by existent id")
    void update_WithExistingId_ReturnAccommodationDto() {
        final Long id = 1L;
        final CreateAccommodationRequestDto requestDto = new CreateAccommodationRequestDto(
                HOUSE,
                "Ukraine",
                "2 floors, 5 rooms",
                List.of("Wi-Fi", "Conditioner", "Parking", "Coffee maker"),
                BigDecimal.valueOf(500),
                3
        );

        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setType(APARTMENT);
        accommodation.setLocation("Italy");
        accommodation.setSize("3 rooms");
        accommodation.setAmenities(new ArrayList<>(List.of("Wi-Fi", "Terrace", "Coffee maker")));
        accommodation.setDailyRate(BigDecimal.valueOf(700));
        accommodation.setAvailability(5);

        when(accommodationRepository.findById(id)).thenReturn(Optional.of(accommodation));
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);

        AccommodationDto accommodationDto = accommodationService.updateById(id, requestDto);

        assertThat(accommodationDto)
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("type", HOUSE)
                .hasFieldOrPropertyWithValue("location", "Ukraine")
                .hasFieldOrPropertyWithValue("size", "2 floors, 5 rooms")
                .hasFieldOrPropertyWithValue("amenities", List.of("Wi-Fi",
                        "Conditioner", "Parking", "Coffee maker"))
                .hasFieldOrPropertyWithValue("dailyRate", BigDecimal.valueOf(500))
                .hasFieldOrPropertyWithValue("availability", 3);
    }

    @Test
    @DisplayName("Delete accommodation by existent id")
    void delete_WithExistingId_ShouldDoNothing() {
        Long id = anyLong();
        when(accommodationRepository.existsById(id)).thenReturn(true);
        doNothing().when(accommodationRepository).deleteById(id);

        accommodationService.deleteById(id);

        verify(accommodationRepository, times(1)).existsById(id);
        verify(accommodationRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(accommodationRepository);
    }

    @Test
    @DisplayName("Delete accommodation using invalid id")
    void delete_WithInvalidId_ShouldThrowException() {
        Long id = -1L;
        when(accommodationRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> accommodationService.deleteById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Accommodation doesn't exist with id %d".formatted(id));

        verify(accommodationRepository, times(1)).existsById(id);
        verifyNoMoreInteractions(accommodationRepository);
    }
}
