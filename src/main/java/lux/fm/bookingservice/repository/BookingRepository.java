package lux.fm.bookingservice.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lux.fm.bookingservice.model.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(attributePaths = "payment")
    Optional<Booking> findByUserEmailAndId(String email, Long id);

    @EntityGraph(attributePaths = "payment")
    List<Booking> findBookingsByUserEmail(String email);

    @EntityGraph(attributePaths = "payment")
    List<Booking> findByUserIdAndStatus(Long userId, Booking.Status stats);

    @EntityGraph(attributePaths = "payment")
    List<Booking> findByStatusAndTimeToLiveBefore(Booking.Status status, LocalTime time);

    @Query("SELECT COUNT(b) from Booking as b where b.accommodation.id = :accommodationId "
            + "AND ((b.checkIn between :checkIn and :checkOut) "
            + "OR (b.checkOut between :checkIn and :checkOut))")
    Long countBookingsInDateRange(Long accommodationId, LocalDate checkIn, LocalDate checkOut);

    @Query("SELECT b FROM Booking as b WHERE b.checkOut = :today ")
    @EntityGraph(attributePaths = "payment")
    List<Booking> checkExpiredBookings(@Param("today") LocalDate today);

    Long countAllByUserEmail(String email);
}
