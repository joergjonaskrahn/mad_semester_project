package org.dailydone.mobile.android.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static long parseStringToUnixTimestamp(String date, String time) throws ParseException {
        // Define the formatter for the input string
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Constants.LOCALE);
        Date parsedDate = formatter.parse(date + " " + time);
        return parsedDate.getTime();
    }
}
