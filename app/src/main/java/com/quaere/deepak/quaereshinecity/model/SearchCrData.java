package com.quaere.deepak.quaereshinecity.model;

/**
 * Created by deepak sachan on 9/14/2015.
 */
public class SearchCrData {
    private String narration;
    private String date;
    private String amount;
    private String type;

    public SearchCrData(String narration, String date, String amount, String type) {
        this.narration = narration;
        this.date = date;
        this.amount = amount;
        this.type = type;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
