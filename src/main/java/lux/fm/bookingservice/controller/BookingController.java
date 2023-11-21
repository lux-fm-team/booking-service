package lux.fm.bookingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.model.Status;
import lux.fm.bookingservice.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}