package lux.fm.bookingservice.notifications;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lux.fm.bookingservice.dto.user.UserLoginRequestDto;
import lux.fm.bookingservice.exception.NotificationException;
import lux.fm.bookingservice.repository.user.UserRepository;
import lux.fm.bookingservice.security.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Getter
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {
    @Value("${bot.username}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
    @Setter
    private String currentCommand = "";
    private final UserRepository userRepository;
    private final List<String> params = new ArrayList<>();
    private final AuthenticationService authenticationService;

    @PostConstruct
    private void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            throw new NotificationException("Can't start telegram bot", e);
        }
    }

    //TODO: refactor code
    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();
        if (update.hasMessage()) {
            sendMessage.setText("Command not recognised");
            sendMessage.setChatId(update.getMessage().getChatId().toString());
            String text = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            if (text.startsWith("/cancel")) {
                currentCommand = "";
                params.clear();
                sendMessage.setText("Current operation ");
            } else if (text.startsWith("/test")) {
                sendMessage.setText("OK");
                sendMessage.setChatId(chatId);
            } else if (text.startsWith("/start")) {
                sendMessage.setText("Press login to start");
                sendMessage.setReplyMarkup(createLoggingKeyboard());
                sendMessage.setChatId(chatId);
            } else if (!currentCommand.isEmpty()) {
                if (currentCommand.equals("login")) {
                    params.add(text);
                    if (params.size() == 1) {
                        sendMessage.setText("Enter your password");
                        sendMessage.setChatId(chatId);
                    } else if (params.size() == 2) {
                        UserLoginRequestDto requestDto =
                                new UserLoginRequestDto(params.get(0), params.get(1));
                        try {
                            authenticationService.authenticate(requestDto);
                            sendMessage.setText("Success!");
                            sendMessage.setChatId(chatId);
                        } catch (BadCredentialsException e) {
                            sendMessage.setText("Wrong email or password!");
                            sendMessage.setChatId(chatId);
                            sendMessage.setReplyMarkup(createLoggingKeyboard());
                        } finally {
                            params.clear();
                            currentCommand = "";
                        }
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            if (callbackQuery.getData().equals("login")) {
                currentCommand = "login";
                sendMessage.setChatId(callbackQuery.getMessage().getChatId().toString());
                sendMessage.setText("Enter your email");
            }
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new NotificationException(
                    "Couldn't send message in chat" + sendMessage.getChatId(), e);
        }
    }

    /**
     * Sending message to user with specified id
     *
     * @param text text of message
     * @param id   users id in telegram
     */

    public void sendMessageToUser(String text, Long id) {
        try {
            execute(new SendMessage(text, id.toString()));
        } catch (TelegramApiException e) {
            throw new NotificationException("Can't send message to user with id " + id);
        }
    }

    private InlineKeyboardMarkup createLoggingKeyboard() {
        InlineKeyboardButton button = new InlineKeyboardButton("Login");
        button.setCallbackData("login");
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
