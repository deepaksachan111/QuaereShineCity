package com.quaere.deepak.quaereshinecity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.quaere.deepak.quaereshinecity.Db.DbHandler;
import com.quaere.deepak.quaereshinecity.DbTable.UserProfile;
import com.squareup.otto.Bus;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {


    private TextView fullname, shortname, authperson, mobileno, phone, address, state, city;
    private TextView emaillllllll;
    public static Handler handler = new Handler();
    public static final Bus BUS = new Bus();
    private static  UserProfileActivity userProfileActivity;
    public static final String TAG = UserProfileActivity.class.getSimpleName();
    // String response;

    @Override
    protected void onResume() {
        BUS.register(this);
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'  size:'2'>User Profile</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));
        setContentView(R.layout.activity_user_profile);
        DbHandler.startIfNotStarted(this);
        //String url = "http://cp.shinecityinfra.in/SCServices.svc/VendorProfile/";
        //   String userProfile = DbHandler.dbHandler.getuserProfileList().getVenderid();
        findviewById();


        String id = DbHandler.dbHandler.getuserProfileList().getVenderid();


        new UserProfileAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
    }

    public static void getString(String s) {
        if(s.equals("[]")) {

           // Toast.makeText(, "invalid username or password", Toast.LENGTH_LONG).show();
        }else {

            JSONParser parser = new JSONParser();
            try {
                Object obj = parser.parse(s);
                JSONArray array = (JSONArray) obj;
                JSONObject obj2 = (JSONObject) array.get(0);
                final String username = (String) obj2.get("FullName");
                final String balance = String.valueOf(obj2.get("VendorBalance"));

                final String venderid = String.valueOf(obj2.get("VendorID"));
                //  DbHandler.dbHandler.saveUserProfile(new UserProfile(venderid, balance));
                final UserProfile userProfile = new UserProfile(username, venderid, balance);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        post(userProfile);
                    }
                });


                Log.v(TAG, "gggggggg" + obj);


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private static void post(UserProfile userProfile) {
        BUS.post(userProfile);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class UserProfileAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(UserProfileActivity.this, "Please Wait", "Connecting", true);
        }


        @Override
        protected String doInBackground(String... params) {

            String url = "http://demo8.mlmsoftindia.com/shinepanel.svc/VendorProfile/" + params[0];

            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("venderid", params[0]));
            String venderid = params[0];
            String s = HttpAgent.post(url);
            return s;
        }


        @Override
        protected void onPostExecute(String res) {

            if (res != null) {
                progressDialog.dismiss();

                try {
                    org.json.JSONArray jsonArray = new org.json.JSONArray(res);
                    org.json.JSONObject jsonObject = (org.json.JSONObject) jsonArray.get(0);
                    String fullName = jsonObject.getString("FullName");
                    String AuthorizedPerson = jsonObject.getString("AuthorizedPerson");
                    String Shortname = jsonObject.getString("DisplayName");
                    String Phone = jsonObject.getString("Phone");
                    String Mobile = jsonObject.getString("Mobile");
                    String Email = jsonObject.getString("Email");
                    String State = jsonObject.getString("State");
                    String City = jsonObject.getString("City");
                    String addresss = jsonObject.getString("Address");

                    fullname.setText(fullName);
                    shortname.setText(Shortname);
                    authperson.setText(AuthorizedPerson);
                    mobileno.setText(Mobile);
                    if (!Email.equals("")) {
                        phone.setText(Phone);
                    }
                    emaillllllll.setText(Email);
                    state.setText(State);
                    city.setText(City);
                    address.setText(addresss);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            super.onPostExecute(res);
        }
    }

    private void findviewById() {

        emaillllllll = (TextView) findViewById(R.id.emailsss);
        fullname = (TextView) findViewById(R.id.full_name);
        shortname = (TextView) findViewById(R.id.short_name);
        authperson = (TextView) findViewById(R.id.auth_person);
        mobileno = (TextView) findViewById(R.id.mobile_no);
        phone = (TextView) findViewById(R.id.phone);
        address = (TextView) findViewById(R.id.address);
        state = (TextView) findViewById(R.id.state);
        city = (TextView) findViewById(R.id.city);

    }
}

