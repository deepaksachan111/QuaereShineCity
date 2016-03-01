package com.quaere.deepak.quaereshinecity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ForgetPasswordActivity extends AppCompatActivity {
    String loginid, mobileno;
    private EditText forgetlogin, forgetmobile;
    private Button forgetsubmit;
  /*  public  static  final Bus BUSDATA = new Bus();
    @Override
    protected void onResume() {
        BUSDATA.register(this);
        super.onResume();
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));
        setContentView(R.layout.activity_forget_password);
        findviewbyid();

        forgetsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginid = forgetlogin.getText().toString();


                forgetlogin.setText("");
                mobileno = forgetmobile.getText().toString();
                forgetmobile.setText("");
                if(!CheckNetwork.isInternetAvailable(ForgetPasswordActivity.this)){
                   Toast toast = Toast.makeText(ForgetPasswordActivity.this, "Internet Unavilable...", Toast.LENGTH_LONG);
                    View view1 = toast.getView();
                    view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast.setGravity(Gravity.TOP,25,600);
                    toast.show();
                }else {
                if (mobileno.length() == 10) {
                    new ForgetAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, loginid, mobileno);
                } else {
                   Toast toast2 = Toast.makeText(ForgetPasswordActivity.this, "enter correct loginid or mobileno.", Toast.LENGTH_LONG);
                    View view1 = toast2.getView();
                    view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast2.setGravity(Gravity.TOP, 25, 600);
                    toast2.show();
                }
            }
            }
        });

    }


    public void findviewbyid() {
        forgetlogin = (EditText) findViewById(R.id.forget_login_id);
        forgetmobile = (EditText) findViewById(R.id.forget_mobileno);
        forgetsubmit = (Button) findViewById(R.id.forget_submit);
    }

    class ForgetAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ForgetPasswordActivity.this, "Please Wait", "Connecting", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String Url = "http://demo8.mlmsoftindia.com/ShinePanel.svc/ForgetPassword/" + params[0] + "/" + params[1];

            String s = HttpAgent.post(Url);
            return s;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            progressDialog.dismiss();
            if (response != null) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject1 = (JSONObject) jsonArray.get(0);
                    String code = jsonObject1.getString("RSCode");
                    if (code.equals("1")) {
                        Intent intent = new Intent(ForgetPasswordActivity.this,OTPPasswordChangeActivity.class);
                        intent.putExtra("loginname",loginid);
                        startActivity(intent);
                       Toast toast = Toast.makeText(ForgetPasswordActivity.this, "Send OTP on your mobile.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 25, 600);
                        View view1 = toast.getView();
                        view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                        toast.show();
                    } else {
                       Toast toast = Toast.makeText(ForgetPasswordActivity.this, "invalid loginid or mobileno.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 25, 600);
                        View view1 = toast.getView();
                        view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

               Toast toast= Toast.makeText(ForgetPasswordActivity.this, "invalid loginid or mobileno.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 600);
                View view1 = toast.getView();
                view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast.show();
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
