package com.quaere.deepak.quaereshinecity.Activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;


import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quaere.deepak.quaereshinecity.Adapter.NavDrawerListAdapter;
import com.quaere.deepak.quaereshinecity.Db.DbHandler;
import com.quaere.deepak.quaereshinecity.Fragment.ChangePasscodeFragment;
import com.quaere.deepak.quaereshinecity.Fragment.ProfileFragment;
import com.quaere.deepak.quaereshinecity.Fragment.TrainingRegistration;
import com.quaere.deepak.quaereshinecity.Fragment.TransactionHistory;
import com.quaere.deepak.quaereshinecity.MainActivity;
import com.quaere.deepak.quaereshinecity.R;
import com.quaere.deepak.quaereshinecity.Utils;
import com.quaere.deepak.quaereshinecity.model.NavDrawerItem;

import java.util.ArrayList;

public class CardLogin extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    private String name;
    private RelativeLayout mDrawerPane;
    final Context context = this;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    private String selectedImagePath;
    private ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setLogo(R.drawable.contact);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Utils.onActivityCreateSetTheme(this);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));




        setContentView(R.layout.activity_card_login);


        TextView tvprofile = (TextView)findViewById(R.id.desc);
        img = (ImageView)findViewById(R.id.circle_background_profile);

        tvprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, 2);
            }
        });

                DbHandler.startIfNotStarted(this);
        name = DbHandler.dbHandler.getcardProfileList().getDisplyname();

    /*  Intent i = getIntent();
      name =  i.getStringExtra("DISPLAYNAME");*/

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        navDrawerItems = new ArrayList<NavDrawerItem>();

        TextView username =(TextView)findViewById(R.id.userName);
        Typeface tf= Typeface.createFromAsset(context.getAssets(), "mexcel3D.ttf");
        username.setTypeface(tf);
        username.setText(name);


        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Photos
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Communities, Will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));




        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
       /* getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);*/


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,

               //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                System.out.println("Image Path : " + selectedImagePath);
                img.setImageURI(selectedImageUri);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onClick(View v) {

    }


    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card_login, menu);
     /*   int positionOfMenuItem = 0; // or whatever...
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString("Hello");
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        item.setTitle(s)*/;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {


            case R.id.logout:
              alertdiaolog();
                return true;

            case R.id.redtheme:
                Utils.changeToTheme(this, Utils.THEME_RED);
                return true;
            case R.id.greentheme:
                Utils.changeToTheme(this, Utils.THEME_GREEN);
                return true;
            case R.id.bluetheme:
                Utils.changeToTheme(this, Utils.THEME_BLUE);
                return true;
            case R.id.defaulttheme:
                Utils.changeToTheme(this, Utils.THEME_DEFAULT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerPane);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ProfileFragment();

                break;
           case 1:
                fragment = new TransactionHistory();
                break;

            case 2:
                fragment = new TrainingRegistration();
                break;

            case 3:
                fragment = new ChangePasscodeFragment();
                break;
            /*
            case 4:
                fragment = new PagesFragment();
                break;
            case 5:
                fragment = new WhatsHotFragment();
                break;*/

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
          /*  mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);*/
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerPane);
        } else {
            // error in creating fragment
            Log.e("Card Login", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

 public  void alertdiaolog(){
     AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
             context);

     // set title
     alertDialogBuilder.setTitle("Exit Application?");

     // set dialog message
     alertDialogBuilder
             .setMessage("Click yes to exit!")
             .setCancelable(false)
             .setPositiveButton("Yes",
                     new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog,
                                             int id) {
                             // if this button is clicked, close
                             // current activity
                             DbHandler.dbHandler.deletecardProfile();
                             startActivity(new Intent(CardLogin.this, MainActivity.class));
                             finish();

                         }
                     })
             .setNegativeButton("No",
                     new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog,
                                             int id) {
                             // if this button is clicked, just close
                             // the dialog box and do nothing
                             dialog.cancel();
                         }
                     });

     // create alert dialog
     AlertDialog alertDialog = alertDialogBuilder.create();

     // show it
     alertDialog.show();
 }
}
