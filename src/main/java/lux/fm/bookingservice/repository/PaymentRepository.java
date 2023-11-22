package lux.fm.bookingservice.repository;

import lux.fm.bookingservice.model.Payment;
import lux.fm.bookingservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Boolean existsPaymentByBookingUserAndStatus(User user, Payment.Status status);
}
