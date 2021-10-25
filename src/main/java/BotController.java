import Database.user.User;
import org.javatuples.Pair;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static Database.user.UserController.*;

public class BotController extends DeliveryBot {
    /* -------------------------------------------------------------------------------------------------------------- */
    /* Send message handlers */

    /**
     * Sending a "text" message to the user
     */
    public void sendTextMessage(Update update, String text) {
        if (update.hasMessage() || update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());
            message.setText(text);

            try {
                execute(message); // Отправка
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * inlineTextAndData - button text and its callback data received
     * posButtons - position of the buttons under the message
     * example: sendInlineMessage(update, "Choose", textAndData, new int[][]{{1,1},{1,0},{1,1}});
     */
    public void sendInlineMessage(Update update, String messageText,
                                  ArrayList<Pair<String, String>> inlineTextAndData, int[][] posButtons) {
        if (update.hasMessage() || update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
            for (Pair<String, String> x : inlineTextAndData) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(x.getValue0());
                inlineKeyboardButton.setCallbackData(x.getValue1());

                inlineKeyboardButtons.add(inlineKeyboardButton);
            }

            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

            for (int[] rows : posButtons) {
                List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
                for (int v : rows) {
                    if (v == 1) {
                        if (inlineKeyboardButtons.size() > 0) {
                            keyboardButtonsRow.add(inlineKeyboardButtons.get(0));
                            inlineKeyboardButtons.remove(0);
                        } else {
                            break;
                        }
                    }
                }
                rowList.add(keyboardButtonsRow);
            }

            inlineKeyboardMarkup.setKeyboard(rowList);

            message.setText(messageText);
            message.setReplyMarkup(inlineKeyboardMarkup);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sending a "text" message with html markup to the user
     */
    public void sendHTMLMessage(Update update, String text) {
        if (update.hasMessage() || update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());
            message.setText(text);

            message.enableHtml(true);
            try {
                execute(message); // Отправка
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sending keyboard with text as title and replyKeyboardMarkup as keyboard settings
     */
    public void sendKeyboard(Update update, String text, ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendMessage message = new SendMessage();

        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(text);
        message.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sending poll with options to the user
     */
    public void sendPoll(Update update, String title, String[] options, boolean multipleAnswers) {
        SendPoll poll = new SendPoll();
        poll.setChatId(update.getMessage().getChatId().toString());
        poll.setQuestion(title);

        ArrayList<String> option = new ArrayList<>(Arrays.asList(options));

        poll.setOptions(option);

        poll.setAllowMultipleAnswers(multipleAnswers);
        poll.setIsAnonymous(false);

        try {
            execute(poll);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /* Received message handlers */

    public void getAnswer(Update u) {
        Message message = u.getMessage();
        String text = message.getText();

        User user = authUser(u); // User authorization, check if banned then return NULL.

        if (text.equalsIgnoreCase("меню") || text.equalsIgnoreCase("/menu") || text.equalsIgnoreCase("/start")) {
            getMenu(u);
        }
    }


    /* -------------------------------------------------------------------------------------------------------------- */
    /* Methods of messages sent */
    private void getMenu(Update update) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow kbFRow = new KeyboardRow();
        KeyboardRow kbSRow = new KeyboardRow();
        KeyboardRow kbTRow = new KeyboardRow();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        kbFRow.add("Доставить");
        kbFRow.add("Заказать");
        kbSRow.add("Активные доставки");
        kbSRow.add("Активные заказы");
        kbTRow.add("Ваши данные");
        kbTRow.add("Команды");
        keyboard.add(kbFRow);
        keyboard.add(kbSRow);
        keyboard.add(kbTRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        sendKeyboard(update, "\u2B07МЕНЮ\u2B07", replyKeyboardMarkup);
    }

    private User authUser(Update update) {
        Message message = update.getMessage();
        User user = getUserById(message.getFrom().getId().toString());

        if (user == null) {
            user = new User();
            //Create new user if not signup
            user.setUserId(message.getFrom().getId().toString());
            user.setFirstName(message.getFrom().getFirstName());
            user.setUsername(message.getFrom().getUserName());
            user.setTimeLastMessage(new Date());
            signUpUser(user);

            System.out.println("\t Text: " + message.getText().substring(0, Math.min(message.getText().length(), 25)));

            return user;
        } else {
            if (user.getStatus().equals("BANNED")) {
                System.out.println("WARNING: A blocked user is trying to write a message.");
                sendHTMLMessage(update, "Вы <b>заблокированы</b> за нарушение правил. \n" +
                        "<u>Если произошла ошибка</u> - @Midell");
                return null;
            } else {
                System.out.println("LOG: Received message from " + message.getFrom().getFirstName() + "(" + message.getChatId() + ")\n" +
                        "\t Text: " + message.getText().substring(0, Math.min(message.getText().length(), 25)));

                if (isFlood(user.getTimeLastMessage())) {
                    user.setWarns(user.getWarns() + 1);
                    if (user.getWarns() % 5 == 0) {
                        sendHTMLMessage(update, "Я не успеваю придумать тебе ответ \uD83D\uDE1E\n" +
                                "Но если ты продолжишь флудить, мне придётся тебя <b>заблокировать</b>. \n" +
                                "Твои предупреждения: <u><b>" + user.getWarns() / 5 + "/3</b></u>");
                        System.out.println("\t WARNING: Flood warning("+ user.getWarns() / 5 + "/3)");
                    }
                    if (user.getWarns() >=15){
                        user.setStatus("BANNED");
                        sendHTMLMessage(update, "Вы были <b>заблокированы</b> за очень частые сообщения.\n\n" +
                                "Блокировка будет снята в <u><b>начале следующего дня</b></u>, если такое повторится - вы будете <b>заблокированы навсегда</b>.\n\n" +
                                "<u>Если произошла ошибка</u> - @Midell");
                        System.out.println("\t WARNING: User has been blocked.");
                    }
                    updateUserDB(user);
                    return null;
                } else {
                    user.setTimeLastMessage(new Date());
                    updateUserDB(user);
                    return user;
                }
            }
        }
    }

    private boolean isFlood(Date date) {
        return Controller.getDateDiff(date, new Date(), TimeUnit.MILLISECONDS) < 500;
    }
}
