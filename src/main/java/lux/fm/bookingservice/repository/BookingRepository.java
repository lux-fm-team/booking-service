package lux.fm.bookingservice.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lux.fm.bookingservice.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByUserEmailAndId(String email, Long id);

    List<Booking> findBookingsByUserEmail(String email);

    List<Booking> findByUserIdAndStatus(Long userId, Booking.Status stats);

    List<Booking> findByStatusAndTimeToLiveBefore(Booking.Status status, LocalTime time);

    @Query("SELECT COUNT(b) from Booking as b where b.accommodation.id = :accommodationId "
            + "AND ((b.checkIn between :checkIn and :checkOut) "
            + "OR (b.checkOut between :checkIn and :checkOut))")
    Long countBookingsInDateRange(Long accommodationId, LocalDate checkIn, LocalDate checkOut);

}
