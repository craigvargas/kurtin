package com.travelguide.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static Date parse(String date) {
        try {
            return getWeekDayDateFormat().parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return getWeekDayDateFormat().format(calendar.getTime());
    }

    public static String formatTime(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        return getTimeFormat().format(calendar.getTime());
    }

    public static int daysDifference(Date startDate, Date endDate) {
        long diff = endDate.getTime() - startDate.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static String formatMonthName(int monthNumber) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, monthNumber);
        return calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
    }

    public static String formatSeasonName(int monthNumber) {
        switch (monthNumber) {
            case 2:
            case 3:
            case 4:
                return "Spring";
            case 5:
            case 6:
            case 7:
                return "Summer";
            case 8:
            case 9:
            case 10:
                return "Fall";
            case 11:
            case 0:
            case 1:
                return "Winter";
        }
        return null;
    }

    private static SimpleDateFormat getTimeFormat() {
        return new SimpleDateFormat("h:mm a", Locale.getDefault());
    }

    private static SimpleDateFormat getWeekDayDateFormat() {
        return new SimpleDateFormat("ccc, MMM d, yyyy", Locale.getDefault());
    }

//    private static SimpleDateFormat getSimpleDateFormat() {
//        return new SimpleDateFormat("MMM/dd/yyyy", Locale.getDefault());
//    }
}
