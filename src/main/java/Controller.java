import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Controller {
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillis = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillis, TimeUnit.MILLISECONDS);
    }

    public static String getRandomCode() {
        String result = "";
        Random r = new Random();

        for (int i = 0; i < 6; i++)
            result += "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(r.nextInt(36));

        return result;
    }

    public static Update changeUpdateText(Update update, String text){
        Message message = update.getMessage();
        if (message == null) message = new Message();
        message.setText(text);
        update.setMessage(message);
        return update;
    }
}
