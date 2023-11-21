package lux.fm.bookingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.booking.BookingRequestUpdateDto;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.exception.BookingInvalidDateException;
import lux.fm.bookingservice.mapper.BookingMapper;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Status;
import lux.fm.bookingservice.repository.booking.BookingRepository;
import lux.fm.bookingservice.service.BookingService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public List<BookingResponseDto> findBookingsByUserIdAndStatus(
            Long userId,
            Status status) {
        return bookingRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> findMyBookings(String username) {
        return bookingRepository.findBookingsByUserEmail(username)
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public BookingResponseDto findBookingById(String username, Long id) {
        Booking booking = bookingRepository.findBookingByUserEmailAndId(username, id).orElseThrow(
                () -> new EntityNotFoundException("There's no booking with id: " + id)
        );
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingResponseDto updateBookingById(BookingRequestUpdateDto requestUpdateDto, Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with such id doesn't exist: " + id)
        );
        validateDates(requestUpdateDto.checkIn(), requestUpdateDto.checkOut(), booking);
        bookingMapper.update(requestUpdateDto, booking);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public void deleteBookingById(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with such id doesn't exist: " + id)
        );
        booking.setStatus(Status.CANCELED);
        bookingRepository.save(booking);
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut, Booking booking) {
        if ((checkIn != null && booking.getCheckOut().isBefore(checkIn))
                || (checkOut != null && booking.getCheckIn().isAfter(checkOut))
        ) {
            throw new BookingInvalidDateException(
                    "CheckOut date can't be earlier than checkIn date"
            );
        }
    }
}
