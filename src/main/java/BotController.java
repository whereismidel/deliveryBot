import Database.user.User;
import org.javatuples.Pair;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
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

    public void deleteMessage(Message message) {
        DeleteMessage dm = new DeleteMessage();
        dm.setMessageId(message.getMessageId());
        dm.setChatId(message.getChatId().toString());

        try {
            execute(dm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void removeKeyboard(Update update, String text) {
        SendMessage message = new SendMessage();
        ReplyKeyboardRemove rk = new ReplyKeyboardRemove();
        rk.setRemoveKeyboard(true);
        message.setReplyMarkup(rk);
        message.setText(text);
        message.setChatId(update.getMessage().getChatId().toString());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    /* -------------------------------------------------------------------------------------------------------------- */
    /* Received message handlers */

    public void getCallbackAnswer(Update update) {
        User user = getUserById(update.getCallbackQuery().getMessage().getChatId().toString());
        String text = update.getCallbackQuery().getData();

        Controller.changeUpdateText(update, text);
        if (user.getStatus().equals("UNREGISTERED")) {
            requestRegistrationData(update, user);
        }

        deleteMessage(update.getCallbackQuery().getMessage());
    }

    public void getAnswer(Update update) {
        Message message = update.getMessage();
        String text = message.getText();

        User user = authUser(update); // User authorization, check if banned then return NULL.

        if (user != null) {
            if (user.getStatus().equals("UNREGISTERED")) {
                requestRegistrationData(update, user);
            } else if (text.equalsIgnoreCase("меню") || text.equalsIgnoreCase("/menu") || text.equalsIgnoreCase("/start")) {
                getMenu(update);
            } else {
                sendTextMessage(update, "Я не знаю что с этим делать \uD83C\uDF1A\nПопробуйте /menu\n");
            }
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
        keyboard.add(kbFRow);
        keyboard.add(kbSRow);
        keyboard.add(kbTRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        sendKeyboard(update, "\u2B07МЕНЮ\u2B07", replyKeyboardMarkup);
    }

    private User authUser(Update update) {
        Message message = update.getMessage();
        if (message.hasContact()) {
            message.setText(message.getContact().getPhoneNumber());
        }
        User user = getUserById(message.getFrom().getId().toString());

        if (user == null) {
            user = new User();
            //Create new user if not signup
            user.setUserId(message.getFrom().getId().toString());
            user.setFirstName(message.getFrom().getFirstName());
            user.setUsername(message.getFrom().getUserName());
            user.setTimeLastMessage(new Date());
            user.setStatus("UNREGISTERED");
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
                        System.out.println("\t WARNING: Flood warning(" + user.getWarns() / 5 + "/3)");
                    }
                    if (user.getWarns() >= 15) {
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
        return Controller.getDateDiff(date, new Date(), TimeUnit.MILLISECONDS) < 400;
    }

    private void requestRegistrationData(Update update, User user) {
        String text = update.getMessage().getText();

        if (user.getStage().contains("Registration")) {
            switch (text) { // Обработка кнопок.
                // Обработка кнопок на отправке письма.
                case "Повторно отправить код": {
                    user.setEmail(user.getEmail().split(":")[0]);
                    user.setStage("Registration(SendEmailConfirmCode)");
                    Controller.changeUpdateText(update, "null");
                    break;
                }
                case "Изменить адрес почты": {
                    user.setStage("Registration(Email)");
                    Controller.changeUpdateText(update, "null");
                    break;
                }
                //**
            }

            user.setTemp(text);
            text = "Регистрация";
        }

        switch (text) {
            case "Регистрация": {
                switch (user.getStage()) {
                    case "NONE": {
                        user.setStage("Registration(FullName)");
                        requestRegistrationData(update, user);
                        break;
                    }
                    case "Registration(FullName)": {
                        if (user.getTemp().equals("Регистрация")) {
                            sendHTMLMessage(update, "Введите ваши <u>Фамилию Имя Отчество</u> в <b>одном сообщении</b>:");
                        } else {
                            if (user.getTemp().matches("^([А-ЯІЇЄ][а-яіїє]*)\\s([А-ЯІЇЄ][а-яіїє]*)((\\s[А-ЯІЇЄ][а-яіїє]*)$|$)")) {
                                user.setFullName(user.getTemp());
                                user.setStage("Registration(Phone)");
                                requestRegistrationData(update, user);
                            } else {
                                sendHTMLMessage(update, "Неверный формат ФИО.\n" +
                                        "Пример: Кіров Сергій Іванович");
                            }
                        }
                        break;
                    }
                    case "Registration(Phone)": {
                        if (user.getTemp().matches("^\\+38\\d{10}$")) {
                            user.setPhone(user.getTemp());
                            user.setStage("Registration(Email)");
                            requestRegistrationData(update, user);
                        } else {
                            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                            ArrayList<KeyboardRow> keyboard = new ArrayList<>();
                            KeyboardRow kbFRow = new KeyboardRow();

                            replyKeyboardMarkup.setSelective(true);
                            replyKeyboardMarkup.setResizeKeyboard(true);
                            replyKeyboardMarkup.setOneTimeKeyboard(true);

                            KeyboardButton keyboardButton = new KeyboardButton();
                            keyboardButton.setText("Отправить контакт");
                            keyboardButton.setRequestContact(true);

                            kbFRow.add(keyboardButton);
                            keyboard.add(kbFRow);
                            replyKeyboardMarkup.setKeyboard(keyboard);

                            sendKeyboard(update, "Введите ваш номер телефона или нажмите кнопку чтобы отправить контакт.\n\n" +
                                    "Пример формата(+38..): +380681234567", replyKeyboardMarkup);
                        }
                        break;
                    }
                    case "Registration(Email)": {
                        if (user.getTemp().matches("^(\\d)+\\w@stud.nau.edu.ua$")) {
                            user.setEmail(user.getTemp());
                            user.setStage("Registration(SendEmailConfirmCode)");
                            requestRegistrationData(update, user);
                        } else {
                            sendHTMLMessage(update, "Введите вашу e-mail почту в домене\n" +
                                    "<b>@stud.nau.edu.ua</b>.\n" +
                                    "На следующем шаге её нужно будет подтвердить...\n\n" +
                                    "Если почта отсутствует - сообщите @Midell");
                        }
                        break;
                    }
                    case "Registration(SendEmailConfirmCode)": {
                        String verificationCode = Controller.getRandomCode();
                        System.out.println("\t Process: Sending email to " + user.getEmail());
                        sendTextMessage(update, "Отправка письма.\n" +
                                "Подождите несколько секунд...");

                        if (EmailSender.sendMail(user.getEmail(), "Код подтверждения", "Твой код для подтверждения почты: " + verificationCode)) {
                            System.out.println("\t\t\t  Successful submission.");
                        } else {
                            System.out.println("\t\t\t  Submission error.");
                        }
                        user.setStage("Registration(GetEmailConfirmCode)");
                        user.setEmail(user.getEmail() + ":" + verificationCode);
                        requestRegistrationData(update, user);
                        break;
                    }
                    case "Registration(GetEmailConfirmCode)": {
                        if (user.getTemp().equals(user.getEmail().split(":")[1])) {
                            removeKeyboard(update, "Вы подтвердили ваш адрес электронной почты.");
                            user.setEmail(user.getEmail().split(":")[0]);
                            user.setStage("Registration(Dorm)");

                            requestRegistrationData(update, user);
                        } else {
                            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                            ArrayList<KeyboardRow> keyboard = new ArrayList<>();
                            KeyboardRow kbFRow = new KeyboardRow();
                            KeyboardRow kbSRow = new KeyboardRow();

                            replyKeyboardMarkup.setSelective(true);
                            replyKeyboardMarkup.setResizeKeyboard(true);
                            replyKeyboardMarkup.setOneTimeKeyboard(true);

                            kbFRow.add("Повторно отправить код");
                            kbSRow.add("Изменить адрес почты");
                            keyboard.add(kbFRow);
                            keyboard.add(kbSRow);
                            replyKeyboardMarkup.setKeyboard(keyboard);

                            sendKeyboard(update, "Введите код, который мы вам отправили на электронную почту, указанную ранее.\n\n" +
                                    "Если код не приходит - @Midell", replyKeyboardMarkup);
                        }
                        break;
                    }
                    case "Registration(Dorm)": {
                        ArrayList<Pair<String, String>> inlineTextAndData = new ArrayList<>();
                        for (int i = 1; i <= 13; i++) {
                            inlineTextAndData.add(new Pair<>("" + i, "DORM" + i));
                        }
                        int[][] position = new int[][]{{1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1}};
                        sendInlineMessage(update, "\u2B07Выберите номер вашего общежития\u2B07", inlineTextAndData, position);
                        user.setStage("Registration(Room)");
                        break;
                    }
                    case "Registration(Room)": {
                        Message msg = update.getMessage();
                        Chat chat = new Chat();

                        chat.setId(Long.parseLong(user.getUserId()));
                        msg.setChat(chat);
                        update.setMessage(msg);
                        System.out.println(user.getDormitory());
                        if (user.getTemp().contains("DORM")) {
                            text = user.getTemp().replace("DORM", "");
                            user.setDormitory(Integer.parseInt(text));
                            sendTextMessage(update, "Вы успешно зарегистрировались.");
                            getMenu(update);
                        }
                        if (user.getDormitory() == 0) {
                            sendTextMessage(update, "\u2B06Выберите ваше общежитие\u2B06");
                            break;
                        }

                        if (user.getTemp().matches("^(\\d{3}+)$")) {
                            user.setRoom(Integer.parseInt(user.getTemp()));
                            user.setTemp("NONE");
                            user.setStage("NONE");
                            user.setStatus("ACTIVE");

                        } else {
                            sendTextMessage(update, "Введите комнату:");
                        }
                        break;
                    }
                }
                updateUserDB(user);
                break;
            }
            case "Информация о боте...\nили зачем мне регистрация?": {
                sendHTMLMessage(update, "Чтобы комфортно пользоваться всеми функциями нам нужны ваши некоторые данные.\n" +
                        "Для обеспечения минимальной гарантии что вы <b>реальный человек</b> и что вашим заказам можно доверять нам необходимо подтверждение <u>вашей личности</u>.\n" +
                        "Дальнейшие инструкции по функциям вы найдете в соответствующих разделах.\n" +
                        "По любым вопросам в работе бота писать - @Midell\n\n" +
                        "Приятного пользования! \uD83DDE09");
                break;
            }
            default: {
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                ArrayList<KeyboardRow> keyboard = new ArrayList<>();
                KeyboardRow kbFRow = new KeyboardRow();
                KeyboardRow kbSRow = new KeyboardRow();

                replyKeyboardMarkup.setSelective(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                replyKeyboardMarkup.setOneTimeKeyboard(true);

                kbFRow.add("Регистрация");
                kbSRow.add("Информация о боте...\n" +
                        "или зачем мне регистрация?");

                keyboard.add(kbFRow);
                keyboard.add(kbSRow);
                replyKeyboardMarkup.setKeyboard(keyboard);

                sendKeyboard(update, "\u2B07Выберите пункт \"Регистрация\"\u2B07", replyKeyboardMarkup);
            }
        }
    }
}
