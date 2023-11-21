package lux.fm.bookingservice.mapper;

import lux.fm.bookingservice.config.MapperConfig;
import lux.fm.bookingservice.dto.accommodation.AccommodationDto;
import lux.fm.bookingservice.model.Accommodation;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AccommodationMapper {
    AccommodationDto toDto(Accommodation accommodation);

    Accommodation toEntity(AccommodationDto accommodationDto);
}
