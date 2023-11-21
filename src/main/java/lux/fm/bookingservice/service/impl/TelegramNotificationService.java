package lux.fm.bookingservice.service.impl;

import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.model.User;
import lux.fm.bookingservice.notifications.Bot;
import lux.fm.bookingservice.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final Bot bot;

    @Override
    public void notifyUser(User user, String message) {
        bot.sendMessageToUser(message, user.getTelegramId());
    }
}
