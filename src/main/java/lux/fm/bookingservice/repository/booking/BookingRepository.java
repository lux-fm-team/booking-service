package lux.fm.bookingservice.repository.booking;

import java.util.List;
import java.util.Optional;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findBookingByUserEmailAndId(String email, Long id);

    List<Booking> findBookingsByUserEmail(String email);

    List<Booking> findByUserIdAndStatus(Long userId, Status stats);


}
