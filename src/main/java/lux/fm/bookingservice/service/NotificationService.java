package lux.fm.bookingservice.service;

import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    void notifyUser(Long id, String message);

    void notifyAllUsers(String message);

}
