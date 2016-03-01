package com.quaere.deepak.quaereshinecity.Db;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.quaere.deepak.quaereshinecity.DbTable.CardProfile;
import com.quaere.deepak.quaereshinecity.DbTable.UserProfile;

import java.sql.SQLException;

/**
 * Created by deepak sachan on 8/28/2015.
 */
public class DbHandler {

    private static final String TAG = DbHandler.class.getSimpleName();
    //  private static final Long LIMIT_ONE = 1L;
    public static DbHandler dbHandler;
    private DbHelper dbHelper = null;
    private Context context;

    private DbHandler(DbHelper databaseHelper, Context context) {
        this.dbHelper = databaseHelper;
        this.context = context;
    }

    public static void start(Context context) {
        if (dbHandler == null) {
            synchronized (DbHandler.class) {
                if (dbHandler == null) {
                    synchronized (DbHandler.class) {
                        DbHelper Dbhelper1 = OpenHelperManager.getHelper(context, DbHelper.class);
                        dbHandler = new DbHandler(Dbhelper1, context);
                    }
                }
            }
        }
    }


    public static void startIfNotStarted(Context context){
        if(dbHandler == null){
            start(context);
        }
    }
    public boolean saveUserProfile(UserProfile profile) {
        try {
            Log.e(TAG, "save userprofile");
            return dbHelper.geUserProfilesDao().createOrUpdate(profile).getNumLinesChanged() == 1;

        } catch (SQLException e) {
            Log.e(TAG, " save Profile......................");
            return false;
        }
    }

    public UserProfile getuserProfileList() {
        try {
            return dbHelper.geUserProfilesDao().queryForId(1);
        } catch (Exception e) {
            Log.e(TAG, " getUserProfile..................");
            return null;
        }
    }
    public void deleteUserProfile() {
        try {
            int k =  dbHelper.geUserProfilesDao().deleteById(1);
            Log.i("", k + "");
        } catch (Exception e) {
            Log.e(TAG, " delete Profile..................");
        }
    }


    public boolean saveCardProfile(CardProfile cardprofile) {
        try {
            Log.e(TAG, "save cardprofile");
            return dbHelper.geCardProfilesDao().createOrUpdate(cardprofile).getNumLinesChanged() == 1;

        } catch (SQLException e) {
            Log.e(TAG, " save cardProfile......................");
            return false;
        }
    }
    public CardProfile getcardProfileList() {
        try {
            return dbHelper.geCardProfilesDao().queryForId(1);
        } catch (Exception e) {
            Log.e(TAG, " getCardProfileList..................");
            return null;
        }
    }
    public void deletecardProfile() {
        try {
            int k =  dbHelper.geCardProfilesDao().deleteById(1);
            Log.i("", k + "");
        } catch (Exception e) {
            Log.e(TAG, " delete card Profile..................");
        }
    }

    }

