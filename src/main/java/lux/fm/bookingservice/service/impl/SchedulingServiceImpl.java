package lux.fm.bookingservice.service.impl;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.repository.BookingRepository;
import lux.fm.bookingservice.service.NotificationService;
import lux.fm.bookingservice.service.SchedulingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulingServiceImpl implements SchedulingService {
    private static final int THREE_MINUTES_IN_MS = 180000;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkExpiredBookings() {
        List<Booking> expiredBookings = bookingRepository.checkExpiredBookings(
                LocalDate.now().minusDays(1)
        );
        if (expiredBookings.isEmpty()) {
            notificationService.notifyAllUsers("""
                    ⚠️No expired bookings since yesterday!""");
        } else {
            expiredBookings.forEach(booking -> {
                booking.setStatus(Booking.Status.EXPIRED);
                notificationService.notifyAboutReleasedAccommodation(booking.getAccommodation());
            });
        }
    }

    @Override
    @Scheduled(fixedRate = THREE_MINUTES_IN_MS)
    public void checkBookingLifeTime() {
        bookingRepository.deleteAll(
                bookingRepository.findByStatusAndTimeToLiveBefore(
                        Booking.Status.PENDING, LocalTime.now()
                )
        );
    }
}
