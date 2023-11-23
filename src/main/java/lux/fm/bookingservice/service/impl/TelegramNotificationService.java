package lux.fm.bookingservice.service.impl;

import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Payment;
import lux.fm.bookingservice.telegram.Bot;
import lux.fm.bookingservice.repository.UserRepository;
import lux.fm.bookingservice.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final Bot bot;
    private final UserRepository userRepository;

    @Override
    public void notifyUser(Long id, String message) {
        bot.sendMessageToUser(message, id);
    }

    public void notifyAllUsers(String message) {
        userRepository.findAllByTelegramIdIsNotNull()
                .forEach(user -> notifyUser(user.getTelegramId(), message));
    }

    @Override
    public void notifyAboutCreatedBooking(Long userId, Booking booking) {
        String message = """
                ✅*You successfully booked accommodation*
                Check info about it
                %s""".formatted(getBookingInfo(booking)
        );
        notifyUser(userId, message);
    }

    @Override
    public void notifyAboutCanceledBooking(Long userId, Booking booking) {
        String message = """
                ⚠️*Your booking was canceled*
                Check info about it
                %s""".formatted(getBookingInfo(booking)
        );
        notifyUser(userId, message);
    }

    @Override
    public void notifyAboutCreatedAccommodation(Accommodation accommodation) {
        String message = """
                🆕*A new accommodation was created*
                %s""".formatted(getAccommodationInfo(accommodation));
        notifyAllUsers(message);
    }

    @Override
    public void notifyAboutReleasedAccommodation(Accommodation accommodation) {
        String message = """
                🆓*A new accommodation is available*
                %s""".formatted(getAccommodationInfo(accommodation));
        notifyAllUsers(message);
    }

    @Override
    public void notifyAboutSuccessPayment(Long userId, Payment payment) {
        // @Todo: Create better message
        notifyUser(userId, """
                ✅Payment for booking %d was successfully completed
                """.formatted(payment.getBooking().getId()));
    }

    private String getBookingInfo(Booking booking) {
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

    private String getAccommodationInfo(Accommodation accommodation) {
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
