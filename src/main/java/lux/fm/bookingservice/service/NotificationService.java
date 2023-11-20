package lux.fm.bookingservice.service;

import lux.fm.bookingservice.model.User;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    void notifyUser(User user, String message);

}
