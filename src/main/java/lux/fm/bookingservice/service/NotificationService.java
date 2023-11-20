package lux.fm.bookingservice.service;

import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    void notifyUser(/*TODO: add User as param*/String message);
    void notifyAllUsers(String message);
}
