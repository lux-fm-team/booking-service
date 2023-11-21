package lux.fm.bookingservice.repository;

import lux.fm.bookingservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
