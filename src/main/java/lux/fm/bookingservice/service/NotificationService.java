package lux.fm.bookingservice.service;

import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Payment;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    void notifyUser(Long id, String message);

    void notifyAllUsers(String message);

    void notifyAboutCreatedBooking(Long userId, Booking booking);

    void notifyAboutCanceledBooking(Long userId, Booking booking);

    void notifyAboutCreatedAccommodation(Accommodation accommodation);

    void notifyAboutReleasedAccommodation(Accommodation accommodation);

    void notifyAboutSuccessPayment(Long userId, Payment payment);
}
