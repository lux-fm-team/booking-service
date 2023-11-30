package lux.fm.bookingservice.service.impl;

import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Payment;
import lux.fm.bookingservice.model.User;
import lux.fm.bookingservice.service.NotificationService;
import lux.fm.bookingservice.service.UserService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final TelegramNotificationService telegramNotificationService;
    private final EmailNotificationService emailNotificationService;

    @Override
    public void notifyUser(User user, String message) {
        emailNotificationService.notifyUser(user, message);
        if (user.getTelegramId() != null) {
            telegramNotificationService.notifyUser(user, message);
        }
    }

    @Override
    public void notifyAllUsers(String message) {
        emailNotificationService.notifyAllUsers(message);
        telegramNotificationService.notifyAllUsers(message);
    }

    @Override
    public void notifyAboutCreatedBooking(User user, Booking booking) {
        emailNotificationService.notifyAboutCreatedBooking(user, booking);
        if (user.getTelegramId() != null) {
            telegramNotificationService.notifyAboutCreatedBooking(user, booking);
        }
    }

    @Override
    public void notifyAboutCanceledBooking(User user, Booking booking) {
        emailNotificationService.notifyAboutCanceledBooking(user, booking);
        if (user.getTelegramId() != null) {
            telegramNotificationService.notifyAboutCanceledBooking(user, booking);
        }
    }

    @Override
    public void notifyAboutCreatedAccommodation(Accommodation accommodation) {
        emailNotificationService.notifyAboutCreatedAccommodation(accommodation);
        telegramNotificationService.notifyAboutCreatedAccommodation(accommodation);
    }

    @Override
    public void notifyAboutReleasedAccommodation(Accommodation accommodation) {
        emailNotificationService.notifyAboutReleasedAccommodation(accommodation);
        telegramNotificationService.notifyAboutReleasedAccommodation(accommodation);
    }

    @Override
    public void notifyAboutSuccessPayment(User user, Payment payment) {
        emailNotificationService.notifyAboutSuccessPayment(user, payment);
        if (user.getTelegramId() != null) {
            telegramNotificationService.notifyAboutSuccessPayment(user, payment);
        }
    }
}
