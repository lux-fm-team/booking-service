package lux.fm.bookingservice.service.impl;

import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Payment;
import lux.fm.bookingservice.model.User;
import lux.fm.bookingservice.repository.UserRepository;
import lux.fm.bookingservice.service.NotificationService;
import lux.fm.bookingservice.telegram.Bot;
import lux.fm.bookingservice.util.NotificationUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final Bot bot;
    private final UserRepository userRepository;
    private final NotificationUtil notificationUtil;

    @Override
    @Async
    public void notifyUser(User user, String message) {
        if (user.getTelegramId() != null) {
            bot.sendMessageToUser(message, user.getTelegramId());
        }
    }

    public void notifyAllUsers(String message) {
        userRepository.findAllByTelegramIdIsNotNull()
                .forEach(user -> notifyUser(user, message));
    }

    @Override
    public void notifyAboutCreatedBooking(User user, Booking booking) {
        String message = """
                ✅*You successfully booked accommodation*
                Check info about it
                %s""".formatted(notificationUtil.getBookingInfo(booking)
        );
        notifyUser(user, message);
    }

    @Override
    public void notifyAboutCanceledBooking(User user, Booking booking) {
        String message = """
                ⚠️*Your booking was canceled*
                Check info about it
                %s""".formatted(notificationUtil.getBookingInfo(booking)
        );
        notifyUser(user, message);
    }

    @Override
    public void notifyAboutCreatedAccommodation(Accommodation accommodation) {
        String message = """
                🆕*A new accommodation was created*
                %s""".formatted(notificationUtil.getAccommodationInfo(accommodation));
        notifyAllUsers(message);
    }

    @Override
    public void notifyAboutReleasedAccommodation(Accommodation accommodation) {
        String message = """
                🆓*A new accommodation is available*
                %s""".formatted(notificationUtil.getAccommodationInfo(accommodation));
        notifyAllUsers(message);
    }

    @Override
    public void notifyAboutSuccessPayment(User user, Payment payment) {
        // @Todo: Create better message
        notifyUser(user, """
                ✅Payment for booking %d was successfully completed
                """.formatted(payment.getBooking().getId()));
    }
}
