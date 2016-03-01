package com.quaere.deepak.quaereshinecity.DbTable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by deepak sachan on 10/3/2015.
 */
@DatabaseTable(tableName = "CardProfile")
public class CardProfile {


    public static final String TAG = CardProfile.class.getSimpleName();
    public static final String FIELD_ID = "id";
    @DatabaseField(id = true)
    private int id;
    @DatabaseField
    private String cardno;
    @DatabaseField
    private String displyname;
    @DatabaseField
    private String totalamount;
    @DatabaseField
    private String redeemaccount;
    @DatabaseField
    private String balance;
    @DatabaseField
    private String memberid;

    public CardProfile(String cardno, String displyname, String totalamount, String redeemaccount,String balance,String memberid) {
        this.id = 1;
        this.cardno = cardno;
        this.displyname = displyname;
        this.totalamount = totalamount;
        this.redeemaccount = redeemaccount;
        this.balance = balance;
        this.memberid = memberid;
    }

    public CardProfile() {

    }

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno;
    }

    public String getDisplyname() {
        return displyname;
    }

    public void setDisplyname(String displyname) {
        this.displyname = displyname;
    }

    public String getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(String totalamount) {
        this.totalamount = totalamount;
    }

    public String getRedeemaccount() {
        return redeemaccount;
    }

    public void setRedeemaccount(String redeemaccount) {
        this.redeemaccount = redeemaccount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }
}
