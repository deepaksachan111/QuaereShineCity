package com.quaere.deepak.quaereshinecity.model;

/**
 * Created by deepak sachan on 9/8/2015.
 */
public class DebitData {

    private String Amount;
    private String DayText;
    private String MonthText;
    private String YearText;

    public DebitData(String amount, String dayText, String monthText, String yearText) {
        this.Amount = amount;
        this.DayText = dayText;
        this.MonthText = monthText;
        this.YearText = yearText;

    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getDayText() {
        return DayText;
    }

    public void setDayText(String dayText) {
        DayText = dayText;
    }

    public String getMonthText() {
        return MonthText;
    }

    public void setMonthText(String monthText) {
        MonthText = monthText;
    }

    public String getYearText() {
        return YearText;
    }

    public void setYearText(String yearText) {
        YearText = yearText;
    }

}
