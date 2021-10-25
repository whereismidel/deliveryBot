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
        new BotController().getAnswer(update);
        /*BotController BOT = new BotController();
        User u = new User();
        org.telegram.telegrambots.meta.api.objects.User m = update.getMessage().getFrom();
        u.setUserId(m.getId().toString());
        u.setFirstName(m.getFirstName());
        u.setUsername(m.getUserName());

        signUpUser(u);
        System.out.println(getUserById("78794393"));
        u.setRoom(123);
        updateUserDB(u);
        u = getUserById(u.getUserId());
        System.out.println(u);*/
    }

}

class BotInitialization {
    public static void main(String[] args) throws InterruptedException {
        while (true) { // Постоянные попытки переподключить бота.
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(new DeliveryBot()); // Запуск бота.

                System.out.println("SUCCESSFUL CONNECTION WITH THE BOT");
/*                User.userList = UserController.getUsers();*/

                return;
            } catch (TelegramApiException e) {
                e.printStackTrace();
                System.out.println("RECONNECTING...");
                TimeUnit.SECONDS.sleep(5);
            }
        }
    }
}
