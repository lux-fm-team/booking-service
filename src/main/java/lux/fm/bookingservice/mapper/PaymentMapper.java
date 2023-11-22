package lux.fm.bookingservice.mapper;

import lux.fm.bookingservice.config.MapperConfig;
import lux.fm.bookingservice.dto.payment.PaymentResponseDto;
import lux.fm.bookingservice.dto.payment.PaymentWithoutSessionDto;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = BookingMapper.class)
public interface PaymentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "amountToPay", source = "booking", qualifiedByName = "totalPrice")
    @Mapping(target = "booking", source = "booking")
    Payment toPayment(Booking booking);

    @Mapping(target = "bookingId", source = "payment.booking.id")
    PaymentResponseDto toDto(Payment payment);

    @Mapping(target = "bookingId", source = "payment.booking.id")
    PaymentWithoutSessionDto toDtoWithoutSession(Payment payment);
}
