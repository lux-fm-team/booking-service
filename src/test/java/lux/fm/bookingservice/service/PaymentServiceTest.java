package lux.fm.bookingservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import com.stripe.exception.StripeException;
import lux.fm.bookingservice.mapper.BookingMapper;
import lux.fm.bookingservice.mapper.PaymentMapper;
import lux.fm.bookingservice.repository.BookingRepository;
import lux.fm.bookingservice.repository.PaymentRepository;
import lux.fm.bookingservice.service.impl.PaymentServiceImpl;
import lux.fm.bookingservice.service.impl.StripeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private StripeService stripeService;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private PaymentServiceImpl paymentService;


    @Test
    void testCreatePayment() throws StripeException {

    }
}
