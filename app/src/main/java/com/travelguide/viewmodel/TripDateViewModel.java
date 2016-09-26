package com.travelguide.viewmodel;

import java.util.Calendar;
import java.util.Date;

import static com.travelguide.helpers.DateUtils.formatDate;
import static com.travelguide.helpers.DateUtils.formatMonthName;
import static com.travelguide.helpers.DateUtils.formatSeasonName;
import static com.travelguide.helpers.DateUtils.formatTime;

public class TripDateViewModel {

    private int year;
    private int monthOfYear;
    private int dayOfMonth;
    private int hourOfDay;
    private int minute;

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getMonthName() {
        return formatMonthName(monthOfYear);
    }

    public String getSeasonName() {
        return formatSeasonName(monthOfYear);
    }

    public String getFormattedDate() {
        return formatDate(year, monthOfYear, dayOfMonth);
    }

    public String getFormattedTime() {
        return formatTime(hourOfDay, minute);
    }

    public int getMonthOfYear() {
        return monthOfYear;
    }

    public Date getParsedDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
        return calendar.getTime();
    }

    @Override
    public String toString() {
        return "TripDateViewModel{" +
                "year=" + year +
                ", monthOfYear=" + monthOfYear +
                ", dayOfMonth=" + dayOfMonth +
                ", hourOfDay=" + hourOfDay +
                ", minute=" + minute +
                '}';
    }
}
