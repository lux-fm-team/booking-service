package lux.fm.bookingservice.mapper;

import lux.fm.bookingservice.config.MapperConfig;
import lux.fm.bookingservice.dto.booking.BookingRequestUpdateDto;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.model.Booking;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {
    @Mapping(source = "booking.accommodation.id", target = "accommodationId")
    BookingResponseDto toDto(Booking booking);

    @BeanMapping(nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE)
    void update(BookingRequestUpdateDto requestUpdateDto, @MappingTarget Booking booking);
}
