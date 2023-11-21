package lux.fm.bookingservice.service.impl;

import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.notifications.Bot;
import lux.fm.bookingservice.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final Bot bot;

    @Override
    public void notifyUser(Long id, String message) {
        bot.sendMessageToUser(message, id);
    }
}
