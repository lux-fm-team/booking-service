package lux.fm.bookingservice.notifications;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Getter
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {
    @Value("${bot.username}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken = "";

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            SendMessage sendMessage = new SendMessage();
            if (text.startsWith("/test")) {
                sendMessage.setText("OK");
                sendMessage.setChatId(chatId);
            } else {
                sendMessage.setText("Command is not in list");
                sendMessage.setChatId(chatId);
            }
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /**
     * Sending message for all users in db
     *
     * @param text text of message
     */

    public void setSendMessageToAll(String text) {
        //TODO: send message for all users
    }

    /**
     * Sending message to user with specified id
     *
     * @param text text of message
     * @param id   users id in telegram
     */

    public void sendMessageToUser(String text, Long id) {
        //TODO: send message for user
    }
}
