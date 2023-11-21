package lux.fm.bookingservice.mapper;

import lux.fm.bookingservice.config.MapperConfig;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.model.Booking;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {
    BookingResponseDto toDto(Booking booking);
}
