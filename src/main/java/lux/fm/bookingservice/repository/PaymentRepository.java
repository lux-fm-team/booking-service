package lux.fm.bookingservice.repository;

import java.util.Optional;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Payment;
import lux.fm.bookingservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findBySessionId(String sessionId);

    Optional<Payment> findBySessionIdAndStatus(String sessionId, Payment.Status status);

    Boolean existsPaymentByBookingUserAndStatus(User user, Payment.Status status);

    Boolean existsByBooking(Booking booking);
}
