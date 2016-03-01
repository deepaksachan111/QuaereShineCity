package com.quaere.deepak.quaereshinecity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.quaere.deepak.quaereshinecity.Activity.CardLogin;
import com.quaere.deepak.quaereshinecity.Db.DbHandler;
import com.quaere.deepak.quaereshinecity.DbTable.CardProfile;
import com.quaere.deepak.quaereshinecity.DbTable.UserProfile;
import com.squareup.otto.Subscribe;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.contact);
        actionBar.setTitle("User Login");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));

        setContentView(R.layout.activity_main);


        DbHandler.startIfNotStarted(this);
        UserProfile userProfile = DbHandler.dbHandler.getuserProfileList();

        CardProfile cardProfile = DbHandler.dbHandler.getcardProfileList();
        if(userProfile!= null){
            startActivity(new Intent(this, PagerslidingActivity.class));
            finish();

        }else if(cardProfile!= null) {
            startActivity(new Intent(this, CardLogin.class));
            finish();
        } else {

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new LoginFragment())
                    .commit();
        }
        }
    }
@Subscribe
public void busdata(UserProfile profile){
    String username = profile.getUsername();
   String id =   profile.getVenderid();
    String balance = profile.getBalance();
    DbHandler.dbHandler.saveUserProfile(new UserProfile(username,id, balance));
}


    @Override
    protected void onPause() {
        UserProfileActivity.BUS.unregister(this);
        super.onPause();
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
