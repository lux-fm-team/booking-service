package lux.fm.bookingservice.controller;

import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.payment.CreatePaymentRequestDto;
import lux.fm.bookingservice.service.impl.StripeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Payment management",
        description = "Endpoints for payments")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Validated
public class PaymentController {
    private final StripeService stripeService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public String createPayment(
            Authentication authentication,
            UriComponentsBuilder uriComponentsBuilder,
            @RequestBody CreatePaymentRequestDto requestDto) throws StripeException {
        return stripeService.createStripeSession(requestDto, uriComponentsBuilder);
    }
}
