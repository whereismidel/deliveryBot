package midel;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillis = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillis, TimeUnit.MILLISECONDS);
    }

    public static Date stringToDate(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("ru"));
        try {
            return format.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        } else {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("ru"));
            return format.format(date);
        }
    }

    public static String getRandomCode() {
        StringBuilder result = new StringBuilder();
        Random r = new Random();

        for (int i = 0; i < 6; i++)
            result.append("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(r.nextInt(36)));

        return result.toString();
    }

    public static void changeUpdateText(Update update, String text) {
        Message message = update.getMessage();
        if (message == null) message = new Message();
        message.setText(text);
        update.setMessage(message);
    }

    public static String replaceLinkOnHyperLink(String text) {
        //<a href="https://example.com">This is an example</a>
        //<a href=\""+text.substring(m.start(), m.end())+">"+"ССЫЛКА(#"+i+")</a>"

        String pattern = "(https?://)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-ZА-Яа-я0-9()@:%_+.~#?&/=]*)";

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        int i = 1;
        while (m.find()) {
            String link = m.group(0);
            text = text.replace(link, "<a href=\"" + link + "\">" + "ССЫЛКА(#" + i + ")</a>");
            i++;
        }

        return text;
    }

    public static boolean isPositiveNumber(String str) {
        return str.matches("^\\d+(\\.\\d+)?$");
    }
}
