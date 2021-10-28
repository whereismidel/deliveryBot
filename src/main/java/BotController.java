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

    /**
     * Delete message
     */
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

    /**
     * Remove keyboard for user
     */
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

    /**
     * --------------------------------------------------------------------------------------------------------------
     * --------------------------------------------------------------------------------------------------------------
     */
    /* Received message handlers */
    public void getCallbackAnswer(Update update) {
        User user = getUserById(update.getCallbackQuery().getMessage().getChatId().toString());
        Message msg = new Message();
        Chat chat = new Chat();

        chat.setId(Long.parseLong(user.getUserId()));
        msg.setChat(chat);
        update.setMessage(msg);

        String text = update.getCallbackQuery().getData();

        Controller.changeUpdateText(update, text);
        if (user.getStatus().equals("UNREGISTERED") || user.getStatus().equals("UPDATE")) {
            requestRegistrationData(update, user);
        } else if (user.getStage().equals("GetInfo")) {
            user.setStage(text);
            user.setStatus("UPDATE");
            Controller.changeUpdateText(update, "Регистрация");
            requestRegistrationData(update, user);
        }

        deleteMessage(update.getCallbackQuery().getMessage());
    }

    public void getAnswer(Update update) {
        Message message = update.getMessage();
        String text = message.getText();

        User user = authUser(update); // User authorization, check if banned then return NULL.

        if (user != null) {
            // Check if the user is registered
            if (user.getStatus().equals("UNREGISTERED")) {
                requestRegistrationData(update, user);
                return;
            }

            // Check command /menu
            switch (text) {
                case "меню":
                case "/menu":
                case "/start":
                case "Вернуться в меню": {
                    user.setStatus("ACTIVE");
                    user.setStage("MENU");
                    user.setTemp("NONE");
                    getMenu(update);
                    updateUserDB(user);
                    return;
                }
            }

            // Check if the user has requested an update / add data
            if (user.getStatus().equals("UPDATE")) {
                requestRegistrationData(update, user);
                return;
            }

            // Stage handler
            switch (user.getStage()) {
                case "NONE": {
                    // Command handler
                    switch (text) {
                        case "/": {
                            break;
                        }

                    }
                    break;
                }
                case "MENU": {
                    menuButtonHandler(update, user);
                    updateUserDB(user);
                    break;
                }
                case "GetInfo": {
                    infoButtonHandler(update, user);
                    break;
                }
            }

        }
    }

    /**
     * --------------------------------------------------------------------------------------------------------------
     * --------------------------------------------------------------------------------------------------------------
     */
    /* Methods of messages sent */
    private boolean isFlood(Date date) {
        return Controller.getDateDiff(date, new Date(), TimeUnit.MILLISECONDS) < 400;
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
                        user.setWarns(0);
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

    private void requestRegistrationData(Update update, User user) {
        String text = update.getMessage().getText();
        boolean updateData = user.getStatus().equals("UPDATE");

        if (user.getStage().contains("Registration")) {
            switch (text) { // Обработка кнопок.
                // Обработка кнопок на отправке письма.
                case "Повторно отправить код": {
                    user.setEmail(user.getEmail().split(":")[0]);
                    user.setStage("Registration(SendEmailConfirmCode)");
                    removeKeyboard(update, "Генерация нового кода..");
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
                        sendHTMLMessage(update, "<b>Если вы сделаете ошибку в ходе регистрации её можно будет исправить позже..</b>");
                        user.setStage("Registration(FullName)");
                        requestRegistrationData(update, user);
                        updateUserDB(user);
                        return;
                    }
                    case "Registration(FullName)": {
                        if (user.getTemp().equals("Регистрация")) {
                            sendHTMLMessage(update, "Введите ваши <u>Фамилию Имя Отчество</u> в <b>одном сообщении</b>:");
                        } else {
                            if (user.getTemp().matches("^([А-ЯІЇЄ][а-яіїє]*)\\s([А-ЯІЇЄ][а-яіїє]*)((\\s[А-ЯІЇЄ][а-яіїє]*)$|$)")) {
                                user.setFullName(user.getTemp());
                                if (updateData) {
                                    user.setStage("Registration(Update)");
                                } else {
                                    user.setStage("Registration(Phone)");
                                }
                                requestRegistrationData(update, user);
                                return;
                            } else {
                                sendHTMLMessage(update, "Неверный формат ФИО.\n" +
                                        "Пример: Кіров Сергій Іванович");
                            }
                        }

                        updateUserDB(user);
                        return;
                    }
                    case "Registration(Phone)": {
                        if (user.getTemp().matches("^(\\+?38)?\\d{10}$")) {
                            user.setPhone(user.getTemp());
                            if (updateData) {
                                user.setStage("Registration(Update)");
                            } else {
                                user.setStage("Registration(Email)");
                            }
                            requestRegistrationData(update, user);
                            return;
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

                        updateUserDB(user);
                        return;
                    }
                    case "Registration(Email)": {
                        if (user.getTemp().matches("^(\\d)+\\w@stud.nau.edu.ua$") && user.getTemp().length() < 45) {
                            user.setEmail(user.getTemp());
                            user.setStage("Registration(SendEmailConfirmCode)");
                            requestRegistrationData(update, user);
                            return;
                        } else {
                            sendHTMLMessage(update, "Введите вашу e-mail почту в домене\n" +
                                    "<b>@stud.nau.edu.ua</b>.");
                            removeKeyboard(update, "На следующем шаге её нужно будет подтвердить...\n\n" +
                                    "Если почта отсутствует - сообщите @Midell");
                        }

                        updateUserDB(user);
                        return;
                    }
                    case "Registration(SendEmailConfirmCode)": {

                        sendTextMessage(update, "Отправка письма.\n" +
                                "Подождите несколько секунд...");

                        if (!(user.getEmail().split(":").length == 2)) {
                            String verificationCode = Controller.getRandomCode();
                            String email = user.getEmail();
                            user.setEmail(user.getEmail() + ":" + verificationCode);
                            System.out.println("\t Process: Sending email to " + user.getEmail());
                            Runnable task = () -> {
                                if (EmailSender.sendMail(email, "Код подтверждения", "Твой код для подтверждения почты: " + verificationCode)) {
                                    System.out.println("\t\t\t  Successful submission.");
                                    user.setStage("Registration(GetEmailConfirmCode)");
                                    requestRegistrationData(update, user);
                                    return;
                                } else {
                                    System.out.println("\t\t\t  Submission error.");
                                    sendTextMessage(update, "Ошибка отправки: возможно такой почты не существует");
                                }
                            };
                            new Thread(task).start();
                        }

                        updateUserDB(user);
                        return;
                    }
                    case "Registration(GetEmailConfirmCode)": {
                        if (user.getTemp().equals(user.getEmail().split(":")[1])) {
                            removeKeyboard(update, "Вы подтвердили ваш адрес электронной почты.");
                            user.setEmail(user.getEmail().split(":")[0]);
                            if (updateData) {
                                user.setStage("Registration(Update)");
                            } else {
                                user.setStage("Registration(Dorm)");
                            }
                            requestRegistrationData(update, user);
                            return;
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

                        updateUserDB(user);
                        return;
                    }
                    case "Registration(Dorm)": {
                        ArrayList<Pair<String, String>> inlineTextAndData = new ArrayList<>();
                        for (int i = 1; i <= 13; i++) {
                            inlineTextAndData.add(new Pair<>("" + i, "DORM" + i));
                        }
                        int[][] position = new int[][]{{1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1}};
                        sendInlineMessage(update, "\u2B07Выберите номер вашего общежития\u2B07", inlineTextAndData, position);

                        user.setStage("Registration(Room)");

                        updateUserDB(user);
                        return;
                    }
                    case "Registration(Room)": {
                        Message msg = update.getMessage();
                        Chat chat = new Chat();

                        chat.setId(Long.parseLong(user.getUserId()));
                        msg.setChat(chat);
                        update.setMessage(msg);

                        if (user.getTemp().contains("DORM")) {
                            text = user.getTemp().replace("DORM", "");

                            user.setDormitory(Integer.parseInt(text));

                            if (updateData) {
                                user.setStage("Registration(Update)");
                                requestRegistrationData(update, user);
                                return;
                            }
                        }
                        if (user.getDormitory() == 0) {
                            sendTextMessage(update, "\u2B06Выберите ваше общежитие\u2B06");
                            break;
                        }

                        if (user.getTemp().matches("^(\\d{3}+)$")) {
                            user.setRoom(Integer.parseInt(user.getTemp()));

                            if (updateData) {
                                user.setStage("Registration(Update)");
                            } else {
                                user.setStage("Registration(End)");
                            }

                            requestRegistrationData(update, user);
                            return;
                        } else {
                            sendTextMessage(update, "Введите комнату:");
                        }

                        updateUserDB(user);
                        return;
                    }
                    case "Registration(End)": {
                        user.setStatus("ACTIVE");
                        user.setStage("MENU");
                        user.setTemp("NONE");
                        sendTextMessage(update, "Вы успешно зарегистрировались.");
                        getMenu(update);

                        updateUserDB(user);
                        return;
                    }
                    case "Registration(Update)": {
                        user.setTemp("NONE");
                        user.setStage("GetInfo");
                        user.setStatus("ACTIVE");
                        sendTextMessage(update, "Ваши данные обновлены.");

                        updateUserDB(user);
                        getUserInfo(update, user);
                        return;
                    }
                }
                return;
            }
            case "Информация о боте...\nили зачем мне регистрация?": {
                sendHTMLMessage(update, "Чтобы комфортно пользоваться всеми функциями нам нужны ваши некоторые данные.\n" +
                        "Для обеспечения минимальной гарантии что вы <b>реальный человек</b> и что вашим заказам можно доверять нам необходимо подтверждение <u>вашей личности</u>.\n" +
                        "Дальнейшие инструкции по функциям вы найдете в соответствующих разделах.\n" +
                        "По любым вопросам в работе бота писать - @Midell\n\n" +
                        "Приятного пользования! \uD83D\uDE09");
                Controller.changeUpdateText(update, "null");
                requestRegistrationData(update, user);
                return;
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

    private void menuButtonHandler(Update update, User user) {
        String text = update.getMessage().getText();
        switch (text) {
            case "Заказать": {
                user.setStage("TODO1");
                break;
            }
            case "Активные заказы": {
                user.setStage("TODO2");
                break;
            }
            case "Доставить": {
                user.setStage("TODO3");
                break;
            }
            case "Активные доставки": {
                user.setStage("TODO4");
                break;
            }
            case "Ваши данные": {
                user.setStage("GetInfo");
                getUserInfo(update, user);
                break;
            }
            default: {
                sendTextMessage(update, "Я не знаю что с этим делать \uD83C\uDF1A\nПопробуйте /menu\n");
            }
        }
    }

    private void getUserInfo(Update update, User user) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow kbFRow = new KeyboardRow();
        KeyboardRow kbSRow = new KeyboardRow();
        KeyboardRow kbTRow = new KeyboardRow();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        kbFRow.add("Мои данные");
        kbSRow.add("Редактировать данные");
        kbSRow.add("Удалить все данные");
        kbTRow.add("Вернуться в меню");
        keyboard.add(kbFRow);
        keyboard.add(kbSRow);
        keyboard.add(kbTRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        sendKeyboard(update, "\u2B07Выберите что нужно сделать\u2B07", replyKeyboardMarkup);
    }

    private void infoButtonHandler(Update update, User user) {
        String text = update.getMessage().getText();
        switch (text) {
            case "Мои данные": {
                String answer = "<b>ФИО</b>: " + user.getFullName() + "\n" +
                        "<b>Номер телефона</b>: " + user.getPhone() + "\n" +
                        "<b>Почта</b>: " + user.getEmail() + "\n" +
                        "<b>Общежитие</b>: №" + user.getDormitory() + "\n" +
                        "<b>Комната</b>: " + user.getRoom() + "\n" +
                        "<b>Предупреждений</b>: " + user.getWarns() / 5 + "/3\n" +
                        "<b>Подписка на доставку</b>: " + (user.isDeliverySub() ? "активирована" : "неактивна") + "\n" +
                        (user.getCardNumber() != null ? "<b>Номер карты</b>: " + user.getCardNumber() + "\n" : "") +
                        (user.getWorkArea() != null ? "<b>Область получения заказов:</b>: " + user.getWorkArea() + "\n" : "") +
                        (user.getDailyEarn() > 0 ? "<b>За сегодня заработано:</b>: " + user.getDailyEarn() + "\n" : "") +
                        (user.getTotalEarn() > 0 ? "<b>Заработано в общем:</b>: " + user.getTotalEarn() + "\n" : "") +
                        (!(user.getRate().split("/")[0].equals("0.0")) ? "<b>Рейтинг доставщика:</b>: " + user.getRate().split("/")[0] + "\n" : "") +
                        (!(user.getRate().split("/")[1].equals("0.0")) ? "<b>Рейтинг заказчика:</b>: " + user.getRate().split("/")[1] + "\n" : "");
                sendHTMLMessage(update, answer);

                getUserInfo(update, user);
                break;
            }
            case "Удалить все данные": {
                user.setStatus("UNREGISTERED");
                user.setStage("NONE");
                removeKeyboard(update, "Жаль что вы нас покинули \uD83D\uDE14");
                requestRegistrationData(update, user);
                updateUserDB(user);
                break;
            }
            case "Редактировать данные": {
                ArrayList<Pair<String, String>> inlineTextAndData = new ArrayList<>();
                inlineTextAndData.add(new Pair<>("ФИО", "Registration(FullName)"));
                inlineTextAndData.add(new Pair<>("Почта", "Registration(Email)"));
                inlineTextAndData.add(new Pair<>("Номер телефона", "Registration(Phone)"));
                inlineTextAndData.add(new Pair<>("Общага", "Registration(Dorm)"));
                inlineTextAndData.add(new Pair<>("Комната", "Registration(Room)"));
                inlineTextAndData.add(new Pair<>("Номер карты", "ToDo")); // ToDO
                inlineTextAndData.add(new Pair<>("Область работы", "ToDo")); // ToDO
                inlineTextAndData.add(new Pair<>("Подписка на доставку", "ToDo")); // ToDO

                int[][] position = new int[][]{{1, 1}, {1}, {1, 1}, {1, 1}, {1}};
                sendInlineMessage(update, "\u2B07Выберите что бы вы хотели изменить\u2B07", inlineTextAndData, position);

                break;
            }
            default: {
                sendTextMessage(update, "Я не знаю что с этим делать \uD83C\uDF1A\nПопробуйте /menu\n");
            }
        }
    }

}
