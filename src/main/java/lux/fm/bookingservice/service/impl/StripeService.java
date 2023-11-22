package lux.fm.bookingservice.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import lux.fm.bookingservice.dto.payment.CreatePaymentRequestDto;
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
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public String createStripeSession(
            CreatePaymentRequestDto requestDto,
            UriComponentsBuilder uriComponentsBuilder) throws StripeException {
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(uriComponentsBuilder.path(SUCCESS_URL).build().toUriString())
                        .setCancelUrl(uriComponentsBuilder.path(CANCEL_URL).build().toUriString())
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
                                                                .setName("Some name")
                                                                .setDescription("Description")
                                                                .build()
                                                )
                                                .build())
                                        .build())
                        .build();
        Session session = Session.create(params);

        return session.getUrl();
    }
}
