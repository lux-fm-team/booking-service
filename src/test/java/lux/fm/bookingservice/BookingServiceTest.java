package lux.fm.bookingservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lux.fm.bookingservice.dto.booking.BookingRequestCreateDto;
import lux.fm.bookingservice.dto.booking.BookingRequestUpdateDto;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.mapper.BookingMapper;
import lux.fm.bookingservice.mapper.impl.BookingMapperImpl;
import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.User;
import lux.fm.bookingservice.repository.AccommodationRepository;
import lux.fm.bookingservice.repository.BookingRepository;
import lux.fm.bookingservice.repository.PaymentRepository;
import lux.fm.bookingservice.repository.UserRepository;
import lux.fm.bookingservice.service.NotificationService;
import lux.fm.bookingservice.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private NotificationService notificationService;
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Accommodation accommodation;
    private Booking booking;
    private BookingRequestCreateDto requestDto;

    @BeforeEach
    void init() {
        user = new User();
        user.setTelegramId(-1L);
        user.setPassword("password_alice");
        user.setRoles(Set.of());
        user.setBooking(List.of());
        user.setEmail("user@gmail.com");
        user.setFirstName("Alice");
        user.setLastName("Blank");
        user.setDeleted(false);
        user.setId(1L);

        accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setBookings(List.of());
        accommodation.setSize(String.valueOf(6));
        accommodation.setDeleted(false);
        accommodation.setType(Accommodation.AccommodationType.HOUSE);
        accommodation.setDailyRate(BigDecimal.TEN);
        accommodation.setAmenities(List.of("WIFI"));
        accommodation.setLocation("LOCATION");
        accommodation.setAvailability(5);

        requestDto = new BookingRequestCreateDto(
                1L,
                LocalDate.parse("2023-11-23"),
                LocalDate.parse("2023-11-24")
        );
        booking = new Booking();
        booking.setUser(user);
        booking.setId(1L);
        booking.setCheckIn(requestDto.checkIn());
        booking.setCheckOut(requestDto.checkOut());
        booking.setAccommodation(accommodation);
        booking.setTimeToLive(LocalTime.now().plusMinutes(5));
    }

    @Test
    @DisplayName("Create new valid booking")
    void create_validBooking_ReturnBookingResponseRto() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toModel(requestDto)).thenReturn(booking);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));
        doNothing().when(notificationService).notifyUser(any(), any());

        BookingResponseDto dto = bookingService.addBooking(authentication, requestDto);
        assertThat(dto)
                .hasFieldOrPropertyWithValue("status", Booking.Status.PENDING)
                .hasFieldOrPropertyWithValue("accommodationId", 1L)
                .hasFieldOrPropertyWithValue("checkIn", requestDto.checkIn())
                .hasFieldOrPropertyWithValue("checkOut", requestDto.checkOut());
    }

    @Test
    @DisplayName("Get non-existing booking")
    void get_nonExistingBookingById_Exception() {
        when(bookingRepository.findByUserEmailAndId(authentication.getName(), 1L))
                .thenReturn(Optional.empty());
        BookingRequestUpdateDto requestUpdateDto = new BookingRequestUpdateDto(
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );

        assertThatThrownBy(() -> bookingService
                .findBookingById(authentication.getName(), 1L))
                .isInstanceOf(EntityNotFoundException.class);
        assertThatThrownBy(() -> bookingService
                .updateBookingById(authentication.getName(), requestUpdateDto, 1L))
                .isInstanceOf(EntityNotFoundException.class);
        assertThatThrownBy(() -> bookingService
                .deleteBookingById(authentication, 1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Get booking by id")
    void get_existingBookingById_ReturnBookingResponseDto() {
        BookingRequestCreateDto requestDto = new BookingRequestCreateDto(
                1L,
                LocalDate.parse("2023-11-23"),
                LocalDate.parse("2023-11-24"));

        booking.setTimeToLive(LocalTime.now().plusMinutes(5));
        List<Booking> bookings = List.of(booking);
        when(bookingRepository.findByUserEmailAndId(user.getEmail(), 1L))
                .thenReturn(Optional.of(bookings.get(0)));
        BookingResponseDto dto = bookingService.findBookingById(user.getEmail(), 1L);
        assertThat(dto)
                .hasFieldOrPropertyWithValue("status", Booking.Status.PENDING)
                .hasFieldOrPropertyWithValue("accommodationId", 1L)
                .hasFieldOrPropertyWithValue("checkIn", requestDto.checkIn())
                .hasFieldOrPropertyWithValue("checkOut", requestDto.checkOut());
    }

    @Test
    @DisplayName("Update existing booking")
    void update_existingBookingById_ReturnBookingResponseDto() {
        BookingRequestUpdateDto requestUpdateDto = new BookingRequestUpdateDto(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(6)
        );
        when(bookingRepository.findByUserEmailAndId(user.getEmail(), 1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        BookingResponseDto responseDto = bookingService
                .updateBookingById(user.getEmail(), requestUpdateDto, 1L);
        assertThat(responseDto)
                .hasFieldOrPropertyWithValue("accommodationId", 1L)
                .hasFieldOrPropertyWithValue("checkIn", requestUpdateDto.checkIn())
                .hasFieldOrPropertyWithValue("checkOut", requestUpdateDto.checkOut());

    }

    @Test
    @DisplayName("Delete existing booking")
    void delete_existingBookingById() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByUserEmailAndId(any(), any()))
                .thenReturn(Optional.of(booking));
        doNothing().when(notificationService).notifyUser(any(), any());
        doNothing().when(bookingRepository).delete(any());
        bookingService.deleteBookingById(authentication, 1L);
    }
}
