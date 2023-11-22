package lux.fm.bookingservice.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lux.fm.bookingservice.dto.payment.PaymentSessionDto;
import lux.fm.bookingservice.exception.PaymentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class StripeService {
    private static final String SUCCESS_URL = "/api/payments/success";
    private static final String CANCEL_URL = "/api/payments/cancel";
    private static final String CURRENCY = "USD";
    private static final BigDecimal CENTS_AMOUNT = BigDecimal.valueOf(100);
    private static final Long DEFAULT_QUANTITY = 1L;
    private static final int EXPIRATION_TIME_IN_MINUTES = 10;
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public Session createStripeSession(
            PaymentSessionDto requestDto,
            UriComponentsBuilder uriComponentsBuilder) {
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setExpiresAt(getExpirationTime())
                        .setSuccessUrl(
                                uriComponentsBuilder.path(SUCCESS_URL).build().toUriString()
                                        + "?session_id={CHECKOUT_SESSION_ID}"
                        )
                        .setCancelUrl(
                                uriComponentsBuilder.path(CANCEL_URL).build().toUriString()
                                        + "?session_id={CHECKOUT_SESSION_ID}"
                        )
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(DEFAULT_QUANTITY)
                                        .setPriceData(SessionCreateParams.LineItem.PriceData
                                                .builder()
                                                .setCurrency(CURRENCY)
                                                .setUnitAmountDecimal(
                                                        requestDto.price().multiply(CENTS_AMOUNT)
                                                )
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData
                                                                .ProductData.builder()
                                                                .setName(requestDto.name())
                                                                .setDescription(
                                                                        requestDto.description())
                                                                .build()
                                                )
                                                .build())
                                        .build())
                        .build();
        try {
            return Session.create(params);
        } catch (StripeException ex) {
            throw new PaymentException("Cant create stripe session");
        }
    }

    // @Todo: Fix expiration
    private long getExpirationTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expirationTime = currentTime.plusMinutes(EXPIRATION_TIME_IN_MINUTES);
        return expirationTime.toEpochSecond(ZoneOffset.UTC);
    }
}
