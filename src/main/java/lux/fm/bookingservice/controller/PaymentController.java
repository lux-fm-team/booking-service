package lux.fm.bookingservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.booking.BookingResponseDto;
import lux.fm.bookingservice.dto.payment.CreatePaymentRequestDto;
import lux.fm.bookingservice.dto.payment.PaymentResponseDto;
import lux.fm.bookingservice.dto.payment.PaymentWithoutSessionDto;
import lux.fm.bookingservice.service.PaymentService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Payment management",
        description = "Endpoints for payments")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Validated
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public PaymentResponseDto createPayment(
            Authentication authentication,
            UriComponentsBuilder uriComponentsBuilder,
            @Valid @RequestBody CreatePaymentRequestDto requestDto) {
        return paymentService.createPayment(authentication, requestDto, uriComponentsBuilder);
    }

    @GetMapping("/success")
    @PreAuthorize("hasRole('CUSTOMER')")
    public PaymentWithoutSessionDto successPayment(@NotBlank String sessionId) {
        return paymentService.successPayment(sessionId);
    }

    @GetMapping("/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public BookingResponseDto cancelPayment(@NotBlank String sessionId) {
        return paymentService.cancelPayment(sessionId);
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public List<PaymentWithoutSessionDto> findUsersPayments(
            @RequestParam(required = false) @Positive Long userId,
            @ParameterObject @PageableDefault Pageable pageable) {
        return paymentService.findByUser(userId, pageable);
    }
}
