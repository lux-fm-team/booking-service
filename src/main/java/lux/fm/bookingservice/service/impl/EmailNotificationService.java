package lux.fm.bookingservice.service.impl;

import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.model.Accommodation;
import lux.fm.bookingservice.model.Booking;
import lux.fm.bookingservice.model.Payment;
import lux.fm.bookingservice.model.User;
import lux.fm.bookingservice.repository.UserRepository;
import lux.fm.bookingservice.service.NotificationService;
import lux.fm.bookingservice.util.NotificationUtil;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmailNotificationService implements NotificationService {
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final NotificationUtil notificationUtil;

    @Override
    public void notifyUser(User user, String message) {
        sendEmail(user.getEmail(), message);
    }

    @Override
    public void notifyAllUsers(String message) {
        userRepository.findAll()
                .forEach(user -> sendEmail(user.getEmail(), message));
    }

    @Override
    public void notifyAboutCreatedBooking(User user, Booking booking) {
        sendEmail(user.getEmail(), """
                Your booking was created:
                %s""".formatted(notificationUtil.getBookingInfo(booking))
        );
    }

    @Override
    public void notifyAboutCanceledBooking(User user, Booking booking) {
        sendEmail(user.getEmail(), """
                Your booking was canceled:
                %s""".formatted(notificationUtil.getBookingInfo(booking))
        );
    }

    @Override
    public void notifyAboutCreatedAccommodation(Accommodation accommodation) {
        userRepository.findAll()
                .forEach(user ->
                        sendEmail(user.getEmail(), """
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
                        sendEmail(user.getEmail(), """
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

    public void sendEmail(String to, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sender@example.com");
        message.setTo(to);
        message.setSubject("Booking.example");
        message.setText(text);
        javaMailSender.send(message);
    }
}
