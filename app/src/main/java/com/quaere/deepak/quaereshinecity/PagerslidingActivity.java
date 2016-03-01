package com.quaere.deepak.quaereshinecity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.quaere.deepak.quaereshinecity.Db.DbHandler;
import com.quaere.deepak.quaereshinecity.DbTable.UserProfile;

public class PagerslidingActivity extends AppCompatActivity {
    String username;
    String id;
    String balance1;
    @Override
    protected void onResume() {
        UserProfileActivity.BUS.register(this);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.contact);
        actionBar.setTitle("Demo");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));*/

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        TextView txtid = (TextView) mCustomView.findViewById(R.id.id);
        TextView balance = (TextView) mCustomView.findViewById(R.id.balance);

        DbHandler.startIfNotStarted(this);
        UserProfile userProfile = DbHandler.dbHandler.getuserProfileList();
        if( userProfile != null) {
            username = DbHandler.dbHandler.getuserProfileList().getUsername();
            id = DbHandler.dbHandler.getuserProfileList().getVenderid();
            balance1 = DbHandler.dbHandler.getuserProfileList().getBalance();
            txtid.setText(username);
            balance.setText(balance1);
        }

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));


      setContentView(R.layout.activity_pagersliding);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.container,new PagerSlidingFragment()).commit();
        }
     /*   ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        if(!CheckNetwork.isInternetAvailable(this)){
            Toast.makeText(this,"Internet Not available",Toast.LENGTH_LONG).show();
        }else {

            viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));

            // Give the PagerSlidingTabStrip the ViewPager
            PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            // Attachthe view pager to the tab strip
            tabsStrip.setViewPager(viewPager);
        }*/
    }


  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pagersliding, menu);
        return true;
    }

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
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();;
    }
}
