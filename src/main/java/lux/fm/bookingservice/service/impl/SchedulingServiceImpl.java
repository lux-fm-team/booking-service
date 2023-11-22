package lux.fm.bookingservice.service.impl;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.repository.BookingRepository;
import lux.fm.bookingservice.service.NotificationService;
import lux.fm.bookingservice.service.SchedulingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulingServiceImpl implements SchedulingService {
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
            String message = "No expired bookings since yesterday!";
            notificationService.notifyAllUsers(message);
            return;
        }

        expiredBookings.stream()
                .forEach(booking -> {
                    booking.setStatus(Booking.Status.EXPIRED);
                    Accommodation accommodation = booking.getAccommodation();
                    String message = """ 
                                    A new accommodation is available!
                                    -type: %s
                                    -location: %2s
                                    -size: %3s
                                    -daily rate: %.2f"""
                                    .formatted(
                                        accommodation.getType().name(),
                                        accommodation.getLocation(),
                                        accommodation.getSize(),
                                        accommodation.getDailyRate().doubleValue()
                                    );
                    notificationService.notifyAllUsers(message);
                });
    }
}
