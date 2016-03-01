package com.quaere.deepak.quaereshinecity.model;

/**
 * Created by deepak sachan on 9/11/2015.
 */
public class Datewisedata {
    private String data;
    private String time;
    private String amount;
  private String type;

    public Datewisedata(String data, String time, String amount, String type) {
        this.data = data;
        this.time = time;
        this.amount = amount;
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
