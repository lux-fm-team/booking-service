package lux.fm.bookingservice.service;

import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.notifications.Bot;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    //TODO: autowire userService
    private final Bot bot;

    @Override
    public void notifyUser(String message /* TODO: add User as param*/) {
       /*
       TODO: add user's id for sending message
       bot.sendMessageToUser(message, user.getTelegramId);
        */
    }

    @Override
    public void notifyAllUsers(String message) {
        bot.setSendMessageToAll(message);
    }
}
