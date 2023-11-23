package lux.fm.bookingservice.repository;

import java.util.Optional;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findBySessionId(String sessionId);

    Optional<Payment> findBySessionIdAndStatus(String sessionId, Payment.Status status);

    Boolean existsPaymentByBookingUserEmailAndStatus(String email, Payment.Status status);

    Boolean existsByBooking(Booking booking);

    @Query("from Payment p join fetch p.booking b join fetch b.user u where u.id = :id")
    Page<Payment> findByUser(Long id, Pageable pageable);
}
