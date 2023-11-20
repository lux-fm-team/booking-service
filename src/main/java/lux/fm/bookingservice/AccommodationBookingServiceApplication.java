package lux.fm.bookingservice;

import lux.fm.bookingservice.notifications.Bot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication

public class AccommodationBookingServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run =
                SpringApplication.run(AccommodationBookingServiceApplication.class, args);
        Bot bot = run.getBean(Bot.class);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
