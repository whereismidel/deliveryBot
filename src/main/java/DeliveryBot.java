import Config.BotConfig;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.concurrent.TimeUnit;

public class DeliveryBot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return BotConfig.BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BotConfig.BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // TODO Вынести список забаненных id -> уменьшить нагрузку на БД.
        if (update.hasCallbackQuery()){
            new BotController().getCallbackAnswer(update);
        }else {
            if (update.hasMessage()) {
                if (!update.getMessage().hasText()){
                    Controller.changeUpdateText(update,"NOT TEXT");
                }
                new BotController().getAnswer(update);
            }
        }
    }

}

class BotInitialization {
    public static void main(String[] args) throws InterruptedException {
        while (true) { // Постоянные попытки переподключить бота.
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(new DeliveryBot()); // Запуск бота.

                System.out.println("SUCCESSFUL CONNECTION WITH THE BOT");

                return;
            } catch (TelegramApiException e) {
                e.printStackTrace();
                System.out.println("RECONNECTING...");
                TimeUnit.SECONDS.sleep(5);
            }
        }
    }
}
