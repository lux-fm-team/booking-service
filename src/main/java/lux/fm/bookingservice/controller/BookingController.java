package lux.fm.bookingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.booking.BookingRequestUpdateDto;
import lux.fm.bookingservice.dto.booking.BookingRequestDto;
import lux.fm.bookingservice.dto.booking.BookingRequestUpdateDto;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.model.Status;
import lux.fm.bookingservice.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Booking management",
        description = "Endpoints for bookings")
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @Operation(
            summary = "Get all bookings with filters",
            description = "Get all bookings by its user_id and status, available for admin only"
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getBookingsByUserIdAndStatus(
            @RequestParam @Positive Long id,
            @RequestParam Status status
    ) {
        return bookingService.findBookingsByUserIdAndStatus(id, status);
    }

    @Operation(
            summary = "Get user bookings",
            description = "Get all bookings for authorized user"
    )
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getMyBookings(
            Authentication authentication
    ) {
        return bookingService.findMyBookings(authentication.getName());
    }

    @Operation(
            summary = "Find booking by id",
            description = "Find booking by id, user have to be authorized"
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto findBookingById(
            Authentication authentication,
            @PathVariable @Positive Long id
    ) {
        return bookingService.findBookingById(authentication.getName(), id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('CUSTOMER')")
    public BookingResponseDto updateBookingById(
            @RequestBody @Valid BookingRequestUpdateDto requestUpdateDto,
            @PathVariable @Positive Long id) {
        return bookingService.updateBookingById(requestUpdateDto, id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public void deleteBookById(@PathVariable @Positive  Long id) {
        bookingService.deleteBookingById(id);
    }

    @Operation(
            summary = "Create a new booking",
            description = "Creates a new booking"
    )
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public BookingResponseDto addBooking(@RequestBody @Valid BookingRequestDto request) {
        return bookingService.addBooking(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('CUSTOMER')")
    public BookingResponseDto updateBookingById(
            @RequestBody @Valid BookingRequestUpdateDto requestUpdateDto,
            @PathVariable @Positive Long id) {
        return bookingService.updateBookingById(requestUpdateDto, id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public void deleteBookById(@PathVariable @Positive Long id) {
        bookingService.deleteBookingById(id);
    }
}
