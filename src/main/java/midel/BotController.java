package midel;

import midel.Database.order.Order;
import midel.Database.order.OrderController;
import midel.Database.order.OrderTable;
import midel.Database.user.User;
import org.javatuples.Pair;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static midel.Database.user.UserController.*;

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
     * buttons - args is a just String as title or button as object.
     * example: sendInlineMessage(update, "Text", new Object[][]{{new Pair<>("Отменить заказ", "CancelOrder#"}});
     * example: sendInlineMessage(update, "Text", new Object[][]{{new InlineKeyboardButton()});
     */
    public int sendInlineMessages(Update update, String messageText, Object[][] buttons) {
        if (update.hasMessage() || update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());
            message.enableHtml(true);

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

            for (Object[] rows : buttons) {
                List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
                for (Object button : rows) {
                    if (button != null) {
                        if (button.getClass() == Pair.class) {
                            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                            Pair<?, ?> pair = (Pair<?, ?>) button;
                            inlineKeyboardButton.setText(String.valueOf(pair.getValue0()));
                            inlineKeyboardButton.setCallbackData(String.valueOf(pair.getValue1()));

                            keyboardButtonsRow.add(inlineKeyboardButton);
                        } else if (button.getClass() == InlineKeyboardButton.class) {
                            keyboardButtonsRow.add((InlineKeyboardButton) button);
                        }
                    }
                }
                rowList.add(keyboardButtonsRow);
            }

            inlineKeyboardMarkup.setKeyboard(rowList);

            message.setText(messageText);
            message.setReplyMarkup(inlineKeyboardMarkup);
            try {
                return execute(message).getMessageId();
            } catch (TelegramApiException e) {
                e.printStackTrace();

            }
        }
        return -1;
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
            message.disableWebPagePreview();
            try {
                execute(message); // Отправка
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sending keyboard with text message as title and buttonAndPosition as keyboard buttons with text and position
     */
    public void sendKeyboard(Update update, String text, Object[][] buttonAndPosition) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        ArrayList<KeyboardRow> keyboard = new ArrayList<>();

        for (Object[] buttonRow : buttonAndPosition) {
            KeyboardRow row = new KeyboardRow();
            for (Object button : buttonRow) {
                if (button.getClass() == String.class) {
                    row.add((String) button);
                } else if (button.getClass() == KeyboardButton.class) {
                    row.add((KeyboardButton) button);
                }
            }
            keyboard.add(row);
        }

        replyKeyboardMarkup.setKeyboard(keyboard);


        SendMessage message = new SendMessage();

        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(text);
        message.enableHtml(true);
        message.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sending location
     */
    public int sendLocation(Update update, String[] coordinates) {
        if (update.hasMessage() || update.getMessage().hasText()) {
            SendLocation location = new SendLocation();
            location.setChatId(update.getMessage().getChatId().toString());
            location.setLatitude(Double.parseDouble(coordinates[0]));
            location.setLongitude(Double.parseDouble(coordinates[1]));

            try {
                return execute(location).getMessageId(); // Отправка
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * Delete message
     */
    public void deleteMessage(String chatId, String messageId) {
        DeleteMessage dm = new DeleteMessage();
        if (messageId.equals("")) return;
        dm.setMessageId(Integer.valueOf(messageId));
        dm.setChatId(chatId);

        try {
            execute(dm);
        } catch (TelegramApiException e) {
            //e.printStackTrace();
            System.out.println("LOG: The message does not exist or has already been deleted.");
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
        message.enableHtml(true);

        message.setChatId(update.getMessage().getChatId().toString());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            System.out.println("LOG: The message does not exist or has already been deleted.");
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
        } else if (user.getStage().contains("CreateOrder")) { // ЧТО ЭТО БЛЯТЬ ДЕЛАЕТ Я ЕБЛАН
            String[] split = text.split("/");
            Message message = update.getMessage();
            int id = Integer.parseInt(split[1]);
            if (id > 0) {
                //message.setMessageId(id);
                deleteMessage(message.getChatId() + "", id + "");
            }
            if (split[0].contains("CreateOrder")) {
                user.setStage(split[0]);
                user.setStatus("ACTIVE(ORDER_CONFIRM)");
            }
            Controller.changeUpdateText(update, split[0]);

            orderCreationProcess(update, user, new Order(user.getTemp().split("\0")));
        } else if (user.getStage().equals("GetInfo")) {
            user.setStage(text);
            user.setStatus("UPDATE");
            Controller.changeUpdateText(update, "Регистрация");
            requestRegistrationData(update, user);
        } else {
            if (text.contains("CancelOrder")) {
                text = text.replace("CancelOrder", "");
                String[] split = text.split("/");
                OrderController.removeOrder(split[0]);
                if (split.length > 1) {
                    deleteMessage(update.getCallbackQuery().getMessage().getChatId() + "", split[1]);
                }
            }
        }


        deleteMessage(update.getCallbackQuery().getMessage().getChatId() + "", update.getCallbackQuery().getMessage().getMessageId() + "");
    }

    public void getAnswer(Update update) {
        Message message = update.getMessage();
        String text = message.getText();

        User user = authUser(update); // User authorization, check if banned then return NULL.

        if (user != null) {
            // Checking if the user is registered
            if (user.getStatus().equals("UNREGISTERED")) {
                requestRegistrationData(update, user);
                return;
            }

            // Checking command /menu
            switch (text) {
                case "меню":
                case "/menu":
                case "/start":
                case "Вернуться в меню": {
                    getMenu(update, user);
                    return;
                }
            }

            // Checking if the user has requested an update / add data
            if (user.getStatus().equals("UPDATE")) {
                requestRegistrationData(update, user);
                return;
            }

            // Checking if the user creates an order
            if (user.getStatus().contains("ACTIVE(ORDER")) {
                orderCreationProcess(update, user, new Order(user.getTemp().split("\0")));
                return;
            }

            // Stage handler
            switch (user.getStage()) {
                case "MENU": {
                    if (!menuButtonHandler(update, user)) { // Если это не кнопка меню, то обработать одну из команд.
                        commandHandler(update);
                    }
                    updateUserDB(user);
                    break;
                }
                case "GetInfo": {
                    infoButtonHandler(update, user);
                    break;
                }
                case "OrderButton": {
                    orderMenuButtonHandler(update, user);
                    break;
                }
                case "GetOrders": {
                    getUserOrders(update, user);
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

    /** Проверяет время от последнего сообщения **/
    private boolean isFlood(Date date) {
        return Controller.getDateDiff(date, new Date(), TimeUnit.MILLISECONDS) < 400;
    }

    /** Авторизирует пользователя,
     * если не зарегистрирован - перебрасывает на этап регистрации,
     * если забанен - блокирует обработку сообщений,
     * если существует - проверяет на флуд, обновляет время последнего сообщения, и пропускает дальше**/
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

    /** Запрос данных пользователя, которые могут использоваться в программе.**/
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
                            KeyboardButton kb = new KeyboardButton();
                            kb.setRequestContact(true);
                            kb.setText("Отправить контакт");
                            sendKeyboard(update,
                                    "Введите ваш номер телефона или нажмите кнопку чтобы отправить контакт.\n\n" +
                                            "Пример формата(+38..): +380681234567",
                                    new Object[][]
                                            {
                                                    {kb}
                                            });
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
                            sendKeyboard(update,
                                    "Введите код, который мы вам <b><u>отправили</u></b> на электронную почту, указанную ранее.\n\n" +
                                            "Если код не приходит - @Midell",
                                    new String[][]{
                                            {"Повторно отправить код"},
                                            {"Изменить адрес почты"}
                                    });
                        }

                        updateUserDB(user);
                        return;
                    }
                    case "Registration(Dorm)": {
                        ArrayList<Pair<String, String>> inline = new ArrayList<>();
                        for (int i = 1; i <= 13; i++) {
                            inline.add(new Pair<>("" + i, "DORM" + i));
                        }
                        int i = 0;
                        sendInlineMessages(update,
                                "\u2B07Выберите номер вашего общежития\u2B07",
                                new Object[][]{
                                        {inline.get(i++), inline.get(i++), inline.get(i++), inline.get(i++)},
                                        {inline.get(i++), inline.get(i++), inline.get(i++), inline.get(i++), inline.get(i++)},
                                        {inline.get(i++), inline.get(i++), inline.get(i++), inline.get(i)}
                                });
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
                        getMenu(update, user);

                        updateUserDB(user);
                        return;
                    }
                    case "Registration(Update)": {
                        user.setTemp("NONE");
                        user.setStage("GetInfo");
                        user.setStatus("ACTIVE");
                        sendTextMessage(update, "Ваши данные обновлены.");

                        updateUserDB(user);
                        getUserInfoMenu(update);
                        return;
                    }

                    //ToDo Добавление карты, изменение подписки,
                    // изменение области работы
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
                sendKeyboard(update,
                        "\u2B07Выберите пункт \"Регистрация\"\u2B07",
                        new String[][]{
                                {"Регистрация"},
                                {"Информация о боте...\n" +
                                        "или зачем мне регистрация?"}
                        });
            }
        }
    }

    /** Отображение кнопок меню **/
    private void getMenu(Update update, User user) {
        if (user.getStage().equals("GetOrders")) {
            // Удаление всех сообщений сделанных заказов, кнопка "Мои заказы"
            for (String id : user.getTemp().split("/")) {
                deleteMessage(update.getMessage().getChatId() + "", id);
            }
        }
        sendKeyboard(update,
                "\u2B07МЕНЮ\u2B07",
                new String[][]{
                        {"Доставить", "Заказать"},
                        {"Ваши данные"}
                });

        user.setStatus("ACTIVE");
        user.setStage("MENU");
        user.setTemp("NONE");
        updateUserDB(user);
    }

    /** Обработка всех кнопок меню **/
    private boolean menuButtonHandler(Update update, User user) {
        String text = update.getMessage().getText();
        switch (text) {
            case "Заказать": {
                user.setStage("OrderButton");
                getOrderMenu(update);
                break;
            }
            case "Доставить": {
                user.setStage("TODO3");
                break;
            }
            case "Ваши данные": {
                user.setStage("GetInfo");
                getUserInfoMenu(update);
                break;
            }
            default:
                return false;
        }
        return true;
    }

    /** Обработка кастомных команд бота **/
    private void commandHandler(Update update) {
        String text = update.getMessage().getText();

        switch (text) {
            case "/test": {
                text = "TEST";
                sendHTMLMessage(update, "Доставщик: " + "<a href=\"tg://user?id=" + 477743708 + "\">" + "соня" + "</a>");
                break;
            }
            case "": {
                break;
            }
            default: {
                sendTextMessage(update, "Я не знаю что с этим делать \uD83C\uDF1A\nПопробуйте /menu\n");
            }
        }
    }

    /** Отображение кнопок информации о пользователе **/
    private void getUserInfoMenu(Update update) {
        sendKeyboard(update,
                "\u2B07Выберите что нужно сделать\u2B07",
                new String[][]{
                        {"Мои данные"},
                        {"Редактировать данные", "Удалить все данные"},
                        {"Вернуться в меню"}
                });
    }

    /** Обработка кнопок информации о пользователе **/
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

                getUserInfoMenu(update);
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
                sendInlineMessages(update,
                        "\u2B07Выберите что бы вы хотели изменить\u2B07",
                        new Object[][]{
                                {new Pair<>("ФИО", "Registration(FullName)"), new Pair<>("Почта", "Registration(Email)")},
                                {new Pair<>("Номер телефона", "Registration(Phone)")},
                                {new Pair<>("Общага", "Registration(Dorm)"), new Pair<>("Комната", "Registration(Room)")},
                                {new Pair<>("Номер карты", "ToDo"), new Pair<>("Область работы", "ToDo")}, // ToDo
                                {new Pair<>("Подписка на доставку", "ToDo")} // ToDo
                        });
                break;
            }
            default: {
                sendTextMessage(update, "Я не знаю что с этим делать \uD83C\uDF1A\nПопробуйте /menu\n");
            }
        }
    }

    /** Отображение кнопок связанных с заказами **/
    private void getOrderMenu(Update update) {
        sendKeyboard(update,
                "\u2B07Выберите что нужно сделать\u2B07",
                new String[][]{
                        {"Новый заказ"},
                        {"Мои заказы", "Что это за раздел?"},
                        {"Вернуться в меню"}
                });
    }

    /** Обработчик кнопок связанных с заказами **/
    private void orderMenuButtonHandler(Update update, User user) {
        String text = update.getMessage().getText();
        switch (text) {
            case "Новый заказ": {
                user.setStage("CreateOrder(RequestPlacePurchase)");
                user.setStatus("ACTIVE(ORDER)");
                orderCreationProcess(update, user, new Order(user.getUserId()));
                break;
            }
            case "Мои заказы": {
                user.setStage("GetOrders");
                getUserOrders(update, user);
                break;
            }
            case "Что это за раздел?": {
                sendHTMLMessage(update, "Тут вы можете сделать заказ."); // ToDo добавить описание
                getOrderMenu(update);
                break;
            }
            default:
                sendTextMessage(update, "Я не знаю что с этим делать \uD83C\uDF1A\nПопробуйте /menu\n");
        }
    }

    /** Создание заказа и добавление его в базу данных **/
    private void orderCreationProcess(Update update, User user, Order order) {
        String text = update.getMessage().getText();
        switch (user.getStage()) {
            case "CreateOrder(RequestPlacePurchase)": {
                sendKeyboard(update, "<u>Напишите</u> место или <u>выберите</u> из доступных ниже <u><b>откуда</b></u> вам нужно что-то доставить..\n\n" +
                                "Кнопка \"Пропустить\" - не имеет значения.",
                        new String[][]{{"ПРОПУСТИТЬ", "Вернуться в меню"}, {"АТБ", "Сільпо", "ЕкоМаркет"}, {"Все для дому"}, {"Шаурма у тройки", "Domino`s Пицца"}});
                user.setStage("CreateOrder(PlacePurchase)");
                user.setTemp(order.toTempString());
                updateUserDB(user);
                break;
            }
            case "CreateOrder(PlacePurchase)": {
                if (text.equalsIgnoreCase("ПРОПУСТИТЬ")) {
                    order.setPlacePurchase("На ваш выбор");
                } else {
                    order.setPlacePurchase(text);
                }
                if (user.getStatus().equals("ACTIVE(ORDER_CONFIRM)")) {
                    user.setStage("CreateOrder(SendReport)");
                } else {
                    user.setStage("CreateOrder(RequestText)");
                }
                user.setTemp(order.toTempString());
                orderCreationProcess(update, user, order);

                break;
            }
            case "CreateOrder(RequestText)": {
                removeKeyboard(update, "Бросьте ссылки<b>(начинаются с http(s)://)</b> на товар или <b>напишите</b> в <u>одном сообщении</u> что бы вы хотели заказать..");
                user.setStage("CreateOrder(WaitText)");
                updateUserDB(user);
                break;
            }
            case "CreateOrder(WaitText)": {
                text = Controller.replaceLinkOnHyperLink(text);
                order.setOrderText(text);
                if (user.getStatus().equals("ACTIVE(ORDER_CONFIRM)")) {
                    user.setStage("CreateOrder(SendReport)");
                } else {
                    user.setStage("CreateOrder(RequestCost)");
                }
                user.setTemp(order.toTempString());
                orderCreationProcess(update, user, order);
                break;
            }
            case "CreateOrder(RequestCost)": {
                sendKeyboard(update,
                        "Введите ориентировочную или точную сумму заказа:",
                        new Object[][]{
                                {"Как работает оплата?"}
                        });

                user.setStage("CreateOrder(WaitCost)");
                updateUserDB(user);
                break;
            }
            case "CreateOrder(WaitCost)": {
                if (text.equals("Как работает оплата?")) {
                    sendHTMLMessage(update, "Чтобы доставщик знал на какую сумму ему рассчитывать вы указываете её на перед.");
                    sendHTMLMessage(update, "После того как на ваш заказ найдется доставщик - у вас будет возможность связаться с ним и договориться как именно вы оплатите свой заказ.");
                    sendHTMLMessage(update, "У вас должна быть полная уверенность, что доставщики проверенные - поэтому для них также есть обязательная верификация.");
                    sendHTMLMessage(update, "По окончанию доставки заказчик будет <b>обязан</b> бросить фото чека/меню/скрин с приложения или другого документа, что подтверждает стоимость покупки.\n" +
                            "Все чеки будут хранится не меньше 7 дней.");
                    sendHTMLMessage(update, "<b><u>Внимание:</u> Комиссию за перечисление средств вы полностью берёте на себя. (Извиняемся за временные трудности)</b>");
                    removeKeyboard(update, "Введите ориентировочную или точную сумму заказа:");
                    break;
                }
                if (Controller.isPositiveNumber(text)) {
                    order.setCost(Double.parseDouble(text));
                } else {
                    sendHTMLMessage(update, "Вы ввели <u>не число, или оно меньше нуля</u>.\n" +
                            "Пример ввода: <b>157.87</b>");
                    break;
                }

                if (user.getStatus().equals("ACTIVE(ORDER_CONFIRM)")) {
                    user.setStage("CreateOrder(SendReport)");
                } else {
                    user.setStage("CreateOrder(DeliveryPlace)");
                }
                user.setTemp(order.toTempString());
                orderCreationProcess(update, user, order);
                break;
            }
            case "CreateOrder(DeliveryPlace)": {
                switch (text) {
                    case "LOCATION":
                        order.setDeliveryPlace("Location:" + update.getMessage().getLocation().getLatitude() + "/" + update.getMessage().getLocation().getLongitude());
                        user.setStage("CreateOrder(SendReport)");
                        user.setTemp(order.toTempString());
                        orderCreationProcess(update, user, order);
                        break;
                    case "Отправить точку на карте":
                        KeyboardButton kb = new KeyboardButton();
                        kb.setRequestLocation(true);
                        kb.setText("Отправить моё местоположение");
                        sendKeyboard(update,
                                "Выполните <b>одно</b> из указанных действий:\n" +
                                        "<b>1.</b> Отправьте через меню Telegram <u>точку на карте</u>\n" +
                                        "<b>2.</b> Нажмите на кнопку чтобы отправить <u>ваше текущее местоположение</u>",
                                new Object[][]{
                                        {kb},
                                        {"Вернуться назад"}
                                });
                        break;
                    default:
                        if (text.equals("Заберу под " + user.getDormitory() + " общагой") ||
                                text.equals("Занести в комнату №" + user.getRoom() + " Общежития №" + user.getDormitory() + "\n(Предупреждение: могут не впустить)")) {
                            order.setDeliveryPlace(text);
                            user.setStage("CreateOrder(SendReport)");
                            user.setTemp(order.toTempString());
                            orderCreationProcess(update, user, order);
                        } else {
                            sendKeyboard(update,
                                    "Выберите куда нужно доставить:",
                                    new String[][]{
                                            {"Заберу под " + user.getDormitory() + " общагой"},
                                            {"Занести в комнату №" + user.getRoom() + " Общежития №" + user.getDormitory() + "\n(Предупреждение: могут не впустить)"},
                                            {"Отправить точку на карте"}
                                    });
                            updateUserDB(user);
                        }
                        break;
                }
                break;
            }
            case "CreateOrder(SendReport)": {
                int idLocation = -1;
                text = "Ваш заказ:\n\n" +
                        "<b>Откуда:</b> " + order.getPlacePurchase() + "\n" +
                        "<b>Куда:</b> ";
                if (order.getDeliveryPlace().contains("Location")) {
                    text += "Точка на карте\n";
                    idLocation = sendLocation(update, order.getDeliveryPlace().replace("Location:", "").split("/"));
                } else {
                    text = text + order.getDeliveryPlace() + "\n";
                }
                text += "<b>Что нужно:</b> " + order.getOrderText() + "\n" +
                        "<b>Ориентировочная стоимость:</b> " + order.getCost() + " грн.";

                InlineKeyboardButton ikb = new InlineKeyboardButton();
                ikb.setText("Текст заказа");
                ikb.setCallbackData("CreateOrder(RequestText)/" + idLocation);
                ikb.setSwitchInlineQueryCurrentChat(order.getOrderText());

                sendInlineMessages(update, text,
                        new Object[][]
                                {
                                        {ikb},
                                        {new Pair<>("Стоимость", "CreateOrder(RequestCost)/" + idLocation)},
                                        {new Pair<>("Место покупки", "CreateOrder(RequestPlacePurchase)/" + idLocation)},
                                        {new Pair<>("Место доставки", "CreateOrder(DeliveryPlace)/" + idLocation)},
                                        {new Pair<>("Подтвердить", "Подтвердить заказ/" + idLocation), new Pair<>("Отменить", "Вернуться в меню/" + idLocation)}
                                });
                user.setStage("CreateOrder(Confirm)");
                removeKeyboard(update, "Если вы допустили ошибку - нажмите на пункт, который хотите исправить.\n" +
                        "Подтвердите/измените/отмените заказ.");
                updateUserDB(user);
                break;
            }
            case "CreateOrder(Confirm)": {
                switch (text) {
                    case "Подтвердить заказ": {
                        order.setDateCreation(new Date());
                        OrderController.createOrder(order);

                        sendTextMessage(update, "Вы подтвердили заказ, ожидайте пока с вами свяжется доставщик");
                        getMenu(update, user);
                        break;
                    }
                    case "Вернуться в меню": {
                        sendTextMessage(update, "Вы отменили заказ.");
                        getMenu(update, user);
                        break;
                    }
                    default: {
                        sendTextMessage(update, "Подтвердите/измените/отмените заказ.");
                    }
                    break;
                }
                break;
            }
        }
    }

    /** Формат отображения заказа для заказчика **/
    private String getOrderFormat(Order order) {
        String result = "Заказ <b>№" + order.getOrderId() + "</b>\n";
        switch (order.getStatus()) {
            case "AVAILABLE":
                result += "Состояние: <b>Доступен</b>\n";
                break;
            case "BLOCKED":
                result += "Состояние: <b>Ожидается подтверждения у доставщика</b>\n";
                break;
            case "TAKEN":
                result += "Состояние: <b>В процессе доставки</b>\n";
                break;
            case "PAID":
                result += "Состояние: <b>Оплачен, подготовлен к получению</b>\n";
                break;
        }
        if (order.getExecutor() == null || order.getExecutor().equals("null")) {
            result += "Доставщик: <b>Отсутствует</b>\n";
        } else {
            String[] exe = order.getExecutor().split("/-ToDo-/"); // ToDo
            result += "Доставщик: <b>" + "<a href=\"tg://user?id=" + exe[0] + "\">" + exe[1] + "</a>" + "</b>\n";
        }
        result += "Откуда: <b>" + order.getPlacePurchase() + "</b>\n";
        if (order.getDeliveryPlace().contains("Location")) {
            result += "Куда: \u2B06<b>Точка на карте</b>\u2B06\n";
        } else {
            result += "Куда: <b>" + order.getDeliveryPlace() + "</b>\n";
        }
        result += "Стоимость: <b>" + order.getCost() + "</b>\n";
        result += "----------\n";
        result += Controller.dateToString(order.getDateCreation()).replace(" ", "\n") + "\n";

        return result;
    }

    /** Предоставление пользователю информации о его заказах с возможностью их удалить **/
    private void getUserOrders(Update update, User user) {
        String text = update.getMessage().getText();
        if (user.getStage().equals("GetOrders") && text.equals("Мои заказы")) {
            ArrayList<Order> orders = OrderController.getOrdersBy(OrderTable.FROM, user.getUserId());
            if (orders.size() == 0) {
                sendHTMLMessage(update, "У вас нет активных заказов.");
                getOrderMenu(update);
                return;
            }
            StringBuilder msgId = new StringBuilder();
            for (Order order : orders) {
                int location = -1;
                if (order.getDeliveryPlace().contains("Location")) {
                    location = sendLocation(update, order.getDeliveryPlace().replace("Location:", "").split("/"));
                    if (location > 0) {
                        msgId.append(location).append("/");
                    }
                }
                int id = sendInlineMessages(update,
                        getOrderFormat(order),
                        new Object[][]{
                                // Отменить заказ по кнопке "Мои заказы" если прикреплена локация, сообщение с ней тоже будет удалено
                                {new Pair<>("Отменить заказ", "CancelOrder" + order.getOrderId() + "/" + (location > 0 ? location : ""))}
                        });
                if (id > 0) {
                    msgId.append(id).append("/");
                }
            }
            user.setTemp(msgId.toString());
            updateUserDB(user);
        } else {
            getMenu(update, user);
        }
        sendKeyboard(update,
                "Если хотите удалить заказ нажмите на кнопку под ним. Доставщик будет уведомлен.",
                new Object[][]{
                        {"Вернуться в меню"}
                });
    }
}
