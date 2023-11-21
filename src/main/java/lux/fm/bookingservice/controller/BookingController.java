package lux.fm.bookingservice.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.model.Status;
import lux.fm.bookingservice.service.BookingService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingResponseDto> getMyBookings(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token
    ) {
        return bookingService.findMyBookings(token);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<BookingResponseDto> getBookingsByUserIdAndStatus(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
            @RequestParam Long id,
            @RequestParam Status status
            ) {
        return bookingService.findBookingsByUserIdAndStatus(token, id, status);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public BookingResponseDto findBookingById(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
            @PathVariable Long id
    ) {
        return null;
    }
}
