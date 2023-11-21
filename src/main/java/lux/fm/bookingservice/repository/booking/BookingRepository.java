package lux.fm.bookingservice.repository.booking;

import java.util.List;
import java.util.Optional;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(attributePaths = "accommodation")
    Optional<List<Booking>> findBookingsByUser(User user);
}
