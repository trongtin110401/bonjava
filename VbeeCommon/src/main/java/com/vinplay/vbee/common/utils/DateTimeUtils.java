/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateTimeUtils {
    public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat df2 = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

    public static Date getSundayEveryWeek() {
        Calendar c = Calendar.getInstance();
        c.set(7, 1);
        c.set(11, 23);
        c.set(12, 59);
        c.set(13, 59);
        c.set(14, 999);
        c.add(5, 7);
        Date date = c.getTime();
        return date;
    }

    public static Date getStartTimeThisMonth() {
        Calendar c = Calendar.getInstance();
        c.set(5, 1);
        c.set(11, 0);
        c.set(12, 0);
        c.set(13, 0);
        c.set(14, 0);
        Date date = c.getTime();
        return date;
    }

    public static Date getEndTimeEveryMonth() {
        Calendar c = Calendar.getInstance();
        c.set(5, c.getActualMaximum(5));
        c.set(11, 23);
        c.set(12, 59);
        c.set(13, 59);
        c.set(14, 999);
        Date date = c.getTime();
        return date;
    }

    public static String getStartTimeToDay() {
        String currentDate = df.format(new Date());
        return currentDate + " 00:00:00";
    }

    public static String getEndTimeToDay() {
        String currentDate = df.format(new Date());
        return currentDate + " 23:59:59";
    }

    public static long getStartTimeToDayAsLong() {
        return DateTimeUtils.getTimeToDayAsLong("00:00:00");
    }

    public static long getEndTimeToDayAsLong() {
        return DateTimeUtils.getTimeToDayAsLong("23:59:59");
    }

    public static long getTimeToDayAsLong(String t) {
        SimpleDateFormat df = new SimpleDateFormat(t + " dd-MM-yyyy");
        String currentDate = df.format(new Date());
        try {
            return df2.parse(currentDate).getTime();
        }
        catch (ParseException parseException) {
            return 0L;
        }
    }

    public static String getToDayAsDate() {
        String currentDate = df.format(new Date());
        return currentDate;
    }

    public static String getCurrentTime() {
        return df2.format(new Date());
    }

    public static String getCurrentTime(String dateFormat) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        return df.format(new Date());
    }

    public static String getFormatTime(String dateFormat, Date date) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        return df.format(date);
    }

    public static long calculateTimeToEndDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        calendar.set(14, 999);
        return calendar.getTimeInMillis() - System.currentTimeMillis();
    }

    public static String getStartTimeThisWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(7, 1);
        String currentDate = df.format(calendar.getTime());
        return currentDate + " 00:00:00";
    }

    public static String getEndTimeThisWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(7, 7);
        String currentDate = df.format(calendar.getTime());
        return currentDate + " 23:59:59";
    }

    public static List<String> getLastTime(Calendar aCalendar) {
        ArrayList<String> res = new ArrayList<String>();
        aCalendar.set(5, 1);
        Date firstDateOfPreviousMonth = aCalendar.getTime();
        aCalendar.set(5, aCalendar.getActualMaximum(5));
        Date lastDateOfPreviousMonth = aCalendar.getTime();
        SimpleDateFormat startFormat = new SimpleDateFormat("yyyy-MM-dd 00;00:00");
        SimpleDateFormat endFormat = new SimpleDateFormat("yyyy-MM-dd 23;59:59");
        String startTime = startFormat.format(firstDateOfPreviousMonth);
        String endTime = endFormat.format(lastDateOfPreviousMonth);
        String month = String.valueOf(aCalendar.get(2) + 1) + "/" + aCalendar.get(1);
        res.add(startTime);
        res.add(endTime);
        res.add(month);
        return res;
    }
}

