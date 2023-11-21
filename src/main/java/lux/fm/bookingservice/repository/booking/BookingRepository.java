package lux.fm.bookingservice.repository.booking;

import java.time.LocalDate;
import java.util.List;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByUserEmail(String email);

    List<Booking> findByUserIdAndStatus(Long userId, Status stats);

    @Query("SELECT COUNT(b) from Booking as b where b.accommodation.id = :accommodationId "
            + "AND ((b.checkIn between :checkIn and :checkOut) "
            + "OR (b.checkOut between :checkIn and :checkOut))")
    Long countBookingsInDate(@Param("accommodationId") Long accommodationId,
                             @Param("checkIn") LocalDate checkIn,
                             @Param("checkOut") LocalDate checkOut);
}
