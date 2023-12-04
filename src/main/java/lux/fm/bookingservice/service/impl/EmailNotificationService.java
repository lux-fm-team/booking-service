package lux.fm.bookingservice.service.impl;

import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Payment;
import lux.fm.bookingservice.model.User;
import lux.fm.bookingservice.repository.UserRepository;
import lux.fm.bookingservice.service.NotificationService;
import lux.fm.bookingservice.util.EmailUtil;
import lux.fm.bookingservice.util.NotificationUtil;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmailNotificationService implements NotificationService {
    private final UserRepository userRepository;
    private final NotificationUtil notificationUtil;
    private final EmailUtil emailUtil;

    @Override
    public void notifyUser(User user, String message) {
        emailUtil.sendEmail(user.getEmail(),"Subject", message);
    }

    @Override
    public void notifyAllUsers(String message) {
        userRepository.findAll()
                .forEach(user -> notifyUser(user, message));
    }

    @Override
    public void notifyAboutCreatedBooking(User user, Booking booking) {
        emailUtil.sendEmail(user.getEmail(),
                "Created new booking",
                notificationUtil.getBookingInfo(booking)
        );
    }

    @Override
    public void notifyAboutCanceledBooking(User user, Booking booking) {
        emailUtil.sendEmail(user.getEmail(), "Canceling booking", """
                Your booking was canceled:
                %s""".formatted(notificationUtil.getBookingInfo(booking))
        );
    }

    @Override
    public void notifyAboutCreatedAccommodation(Accommodation accommodation) {
        userRepository.findAll()
                .forEach(user ->
                        emailUtil.sendEmail(user.getEmail(), "Accommodation", """
                                        Following accommodation was created:
                                        %s""".formatted(
                                        notificationUtil.getAccommodationInfo(accommodation)
                                )
                        )
                );
    }

    @Override
    public void notifyAboutReleasedAccommodation(Accommodation accommodation) {
        userRepository.findAll()
                .forEach(user ->
                        emailUtil.sendEmail(user.getEmail(),
                                "Releasing accommodation",
                                        """
                                        Following accommodation now have empty slots
                                        %s""".formatted(
                                        notificationUtil.getAccommodationInfo(accommodation)
                                )
                        )
                );
    }

    @Override
    public void notifyAboutSuccessPayment(User user, Payment payment) {

    }
}
