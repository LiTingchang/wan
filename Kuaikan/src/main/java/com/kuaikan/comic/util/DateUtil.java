package com.kuaikan.comic.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by skyfishjy on 12/20/14.
 */
public class DateUtil {

    public static final String[] chineseMonths = {"1月", "2月", "3月", "4月",
            "5月", "6月", "7月", "8月",
            "9月", "10月", "11月", "12月"};
    public static final String[] chineseWeeks = {"", "星期日", "星期一", "星期二",
            "星期三", "星期四", "星期五",
            "星期六"};

    public static Calendar convertUNIX2Calendar(long unixtime) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(unixtime * 1000);
        return date;
    }

    public static final int    SECOND       = 1000;
    public static final int    MINUTE       = 60 * SECOND;
    public static final int    HOUR         = 60 * MINUTE;

    public static String covenrtCommentUNIX2SimpleString(long unixtime) {
        Calendar calendar = convertUNIX2Calendar(unixtime);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        long delta = new Date().getTime() - calendar.getTime().getTime();

        StringBuffer timeString = new StringBuffer();

        if(delta < MINUTE){
            timeString.append("刚刚");
        }else if(delta > MINUTE && delta < HOUR){
            int value = (int) (delta / MINUTE);
            timeString.append(value + "分钟前");
        }else{
            if (month + 1 < 10)
                timeString.append("0");

            timeString.append((month + 1) + "-");

            if (day < 10)
                timeString.append("0");

            timeString.append(day + " ");

            if (hour < 10)
                timeString.append("0");

            timeString.append(hour + ":");

            if (minute < 10)
                timeString.append("0");

            timeString.append(minute);
        }


        return timeString.toString();
    }

    public static String covenrtUNIX2String(long unixtime) {
        Calendar calendar = convertUNIX2Calendar(unixtime);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String timeString = "";
        if (month + 1 < 10)
            timeString = "0";

        timeString += (month + 1) + "月";

        if (day < 10)
            timeString += "0";

        timeString += day + "日 ";

        if (hour < 10)
            timeString += "0";

        timeString += hour + ":";

        if (minute < 10)
            timeString += "0";

        timeString += minute;

        return timeString;
    }

    public static String formatSectionHeaderDate(Calendar calendar){
        return chineseMonths[calendar.get(Calendar.MONTH)] + calendar.get(Calendar.DAY_OF_MONTH) + "日";
    }

    public static String formatSectionHeaderWeek(Calendar calendar) {
        return chineseWeeks[calendar.get(Calendar.DAY_OF_WEEK)];
    }
}
