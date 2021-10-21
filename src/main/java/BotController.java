import org.javatuples.Pair;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BotController extends DeliveryBot{
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
     * inlineTextAndData - button text and its callbackdata received
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
            for(Pair<String, String> x : inlineTextAndData) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(x.getValue0());
                inlineKeyboardButton.setCallbackData(x.getValue1());

                inlineKeyboardButtons.add(inlineKeyboardButton);
            }

            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

            for(int[] rows : posButtons){
                List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
                for(int v : rows){
                    if (v == 1){
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
    public void sendHTMLMessage(Update update, String text){
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
    public void sendKeyboard(Update update, String text, ReplyKeyboardMarkup replyKeyboardMarkup){
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
    public void sendPoll(Update update, String title, String[] options, boolean multipleAnswers){
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



    /* -------------------------------------------------------------------------------------------------------------- */

}
