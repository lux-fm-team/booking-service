package lux.fm.bookingservice.service;

import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Payment;
import lux.fm.bookingservice.model.User;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
public interface NotificationService {
    void notifyUser(User user, String message);

    void notifyAllUsers(String message);

    void notifyAboutCreatedBooking(User user, Booking booking);

    void notifyAboutCanceledBooking(User user, Booking booking);

    void notifyAboutCreatedAccommodation(Accommodation accommodation);

    void notifyAboutReleasedAccommodation(Accommodation accommodation);

    void notifyAboutSuccessPayment(User user, Payment payment);
}
