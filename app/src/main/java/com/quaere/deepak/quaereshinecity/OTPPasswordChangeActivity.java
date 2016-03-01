package com.quaere.deepak.quaereshinecity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OTPPasswordChangeActivity extends AppCompatActivity {
    private EditText otp, newpass, confirmpass;
    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'  size:'2'>Change Password</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));
        setContentView(R.layout.activity_otppassword_change);
        otp = (EditText) findViewById(R.id.edt_otp);
        newpass = (EditText) findViewById(R.id.edt_newpass);
        confirmpass = (EditText) findViewById(R.id.edt_confirmpass);
      //  DbHandler.startIfNotStarted(this);
        send = (Button) findViewById(R.id.otp_send);

        send.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent intent = getIntent();

                String username = intent.getStringExtra("loginname");
                String getotp = otp.getText().toString();
                String username1 = username;
                 if(newpass.length() <4){
                     Toast toast= Toast.makeText(OTPPasswordChangeActivity.this, "Enter Valid loginId", Toast.LENGTH_LONG);
                     View tostview = toast.getView();
                     tostview.setBackgroundResource(R.drawable.toast_drawablecolor);
                     toast.setGravity(Gravity.TOP, 25, 500);
                     toast.show();
                 }
                if(!confirmpass.equals(newpass)){
                    Toast toast= Toast.makeText(OTPPasswordChangeActivity.this, "confirm password not match", Toast.LENGTH_LONG);
                    View tostview = toast.getView();
                    tostview.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast.setGravity(Gravity.TOP, 25, 500);
                    toast.show();
                }

                new OTPChangePassAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, username, getotp);
            }
        });

    }

  private  void findviewbyid() {


  }


    class  OTPChangePassAsyncTask extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String url ="http://demo8.mlmsoftindia.com/ShinePanel.svc/ValidateOTP/"+params[0]+"/"+params[1];
                String res = HttpAgent.post(url);
            return res;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if(response!= null){
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = (JSONObject)jsonArray.get(0);
                    String rs_code = jsonObject.getString("RSCode");
                    if(rs_code.equals("1")){
                        Intent intent = getIntent();
                        String username =   intent.getStringExtra("loginname");
                            String NewPass = newpass.getText().toString();


                            String ConfirmPass= confirmpass.getText().toString();
                        confirmpass.setText("");
                        if(NewPass.length() < 4){
                            newpass.requestFocus();
                         Toast toast =   Toast.makeText(OTPPasswordChangeActivity.this, "Password is Too Short", Toast.LENGTH_LONG);
                            View view1 = toast.getView();
                            view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                            toast.setGravity(Gravity.TOP, 25, 500);
                            toast.show();

                        }
                          if(!NewPass.equals(ConfirmPass)){
                              confirmpass.setText("");
                              confirmpass.requestFocus();
                           Toast toast= Toast.makeText(OTPPasswordChangeActivity.this, "Confirm Password not match", Toast.LENGTH_LONG);
                              toast.setGravity(Gravity.TOP, 25, 500);
                              View view1 = toast.getView();
                              view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                              toast.show();

                        }else {
                          new SetPassword().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,username,NewPass);
                        }

                    }else {
                        otp.setText("");
                        otp.setFocusableInTouchMode(true);
                        otp.requestFocus();
                       Toast toast = Toast.makeText(OTPPasswordChangeActivity.this, "Inavalid OTP code", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 25, 500);
                        View view1 = toast.getView();
                        view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }


    }

    class SetPassword extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String url = "http://demo8.mlmsoftindia.com/ShinePanel.svc/SetPassword/"+params[0]+ "/" +params[1];
           String res=  HttpAgent.post(url);
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null){
               Toast toast = Toast.makeText(OTPPasswordChangeActivity.this, "Successfully Password Changed", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 500);
                View view1 = toast.getView();
                view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast.show();
                startActivity(new Intent(OTPPasswordChangeActivity.this,MainActivity.class));
            }
        }
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
}
