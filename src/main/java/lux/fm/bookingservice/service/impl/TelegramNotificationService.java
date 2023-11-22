package lux.fm.bookingservice.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.model.User;
import lux.fm.bookingservice.notifications.Bot;
import lux.fm.bookingservice.repository.UserRepository;
import lux.fm.bookingservice.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final Bot bot;
    private final UserRepository userRepository;

    @Override
    public void notifyUser(Long id, String message) {
        bot.sendMessageToUser(message, id);
    }

    public void notifyAllUsers(String message) {
        List<User> telegramUsers = userRepository.findAllByTelegramIdIsNotNull();

        if (telegramUsers.isEmpty()) {
            return;
        }

        telegramUsers.stream()
                .map(User::getTelegramId)
                .forEach(chatId -> notifyUser(chatId, message));
    }
}
