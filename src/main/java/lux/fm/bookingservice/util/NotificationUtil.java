package lux.fm.bookingservice.util;

import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.model.Booking;
import org.springframework.stereotype.Component;

@Component
public class NotificationUtil {
    public String getBookingInfo(Booking booking) {
        Accommodation accommodation = booking.getAccommodation();
        return """
                — 🌆`Location:` %s
                — 🏚`Type:` %s
                — 📅`Check in`: %s
                — 📅`Check out:` %s
                — 💰`Total:` $%.2f
                """.formatted(
                accommodation.getLocation(),
                String.valueOf(accommodation.getType()),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getTotal()
        );
    }

    public String getAccommodationInfo(Accommodation accommodation) {
        return """ 
                — 🏚`Type:` %s
                — 🌆`Location:` %2s
                — 🚻`Size:` %3s
                — 💸`Daily rate:` $%.2f"""
                .formatted(
                        accommodation.getType().name(),
                        accommodation.getLocation(),
                        accommodation.getSize(),
                        accommodation.getDailyRate().doubleValue()
                );
    }
}
