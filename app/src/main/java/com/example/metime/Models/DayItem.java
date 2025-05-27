package com.example.metime.Models;

import java.util.Calendar;

public class DayItem {
    private int dayOfMonth;
    private String dayName;
    private boolean isSelected;
    private Calendar date;

    public DayItem(int dayOfMonth, String dayName, Calendar date) {
        this.dayOfMonth = dayOfMonth;
        this.dayName = dayName;
        this.date = date;
        this.isSelected = false;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public String getDayName() {
        return dayName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Calendar getDate() {
        return date;
    }
}