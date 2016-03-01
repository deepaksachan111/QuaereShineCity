package com.quaere.deepak.quaereshinecity.DbTable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "UserProfile")
public class UserProfile {

    public static final String TAG = UserProfile.class.getSimpleName();
    public static final String FIELD_ID = "id";
    @DatabaseField(id = true)
    private int id;
    @DatabaseField
    private String username;
    @DatabaseField
    private String venderid;
    @DatabaseField
    private String balance;




    public UserProfile(String username,String venderid, String balance) {
        this.id = 1;
        this.username = username;

        this.venderid = venderid;
        this.balance = balance;
    }

    public UserProfile() {

    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getVenderid() {
        return venderid;
    }

    public void setVenderid(String venderid) {
        venderid = venderid;
    }

    public String getBalance() {
        return balance;
    }

    public void setUserPassword(String balance) {
        balance = balance;
    }
}