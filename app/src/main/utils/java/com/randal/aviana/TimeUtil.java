package com.randal.aviana;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yanbinbin on 16/7/27.
 */
public final class TimeUtil {

    private TimeUtil(){
        throw new UnsupportedOperationException("DO NOT INSTANTIATE THIS CLASS");
    }

    public static long currentTimestamp(){
        return System.currentTimeMillis();
    }

    /**
     * Parse the format string to calendar
     */
    public static Calendar string2calendar(String str, String format) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            LogUtils.w("Parse Exception!" + str);
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Parse the calendar to format string
     */
    public static String calendar2string(Calendar cal, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

    /**
     * Parse the format string to the timestamp
     */
    public static long string2timestamp(String str, String format) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            LogUtils.w("Parse Exception!" + str);
            e.printStackTrace();
            return 0;
        }
        return date.getTime();
    }

    /**
     * Parse the calendar to the timestamp
     */
    public static long calendar2timestamp(Calendar cal) {
        return cal.getTimeInMillis();
    }

    /**
     * Parse the timestamp to the format date
     */
    public static String timestamp2string(long timestamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(timestamp);
    }

    /**
     * Parse the timestamp to calendar
     */
    public static Calendar timestamp2calendar(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar;
    }

    /**
     * Parse the duration to timestamp
     */
    public static long timeLengthToTimestamp(String timeLength){
        String[] times = timeLength.split("分钟");
        String time = times[0];
        long realTime = Long.valueOf(time) * 60 * 1000;
        return realTime;
    }
}
