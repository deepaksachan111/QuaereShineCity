package com.quaere.deepak.quaereshinecity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.quaere.deepak.quaereshinecity.Db.DbHandler;
import com.quaere.deepak.quaereshinecity.model.DebitData;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText oldpass, newpass, confirmpass;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'  size:'2'>Change Password</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));
        setContentView(R.layout.activity_change_password);

        oldpass = (EditText) findViewById(R.id.edt_oldpass);
        newpass = (EditText) findViewById(R.id.edt_changenewpass);
        confirmpass = (EditText) findViewById(R.id.edt_changeconfirmpass);
        submit = (Button) findViewById(R.id.btn_change_submit);
        DbHandler.startIfNotStarted(this);


        submit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String oldp = oldpass.getText().toString();
                String newp = newpass.getText().toString();
                String confirmp = confirmpass.getText().toString();
                if (oldp.length() < 4) {
                    Toast toast = Toast.makeText(ChangePasswordActivity.this, "enter OldPassword", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 600);
                    View view1 = toast.getView();
                    view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast.show();
                } else if (newp.length() < 4) {
                    Toast toast2 = Toast.makeText(ChangePasswordActivity.this, "enter newpassword atleast 4 digit", Toast.LENGTH_LONG);
                    View view1 = toast2.getView();
                    view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast2.setGravity(Gravity.TOP, 25, 600);
                    toast2.show();
                } else if (!newp.equals(confirmp)) {
                    Toast toast3 = Toast.makeText(ChangePasswordActivity.this, "newpass && confirmpass not match", Toast.LENGTH_LONG);
                    View view1 = toast3.getView();
                    view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast3.setGravity(Gravity.TOP, 25, 600);
                    toast3.show();
                } else {

                    String vendorid = DbHandler.dbHandler.getuserProfileList().getVenderid();
                    String url = "http://demo8.mlmsoftindia.com/ShinePanel.svc/VendorPassword/" + vendorid + "/" + oldp + "/" + newp;
                    new ChangePasswordAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
                }
            }
        });

    }

    private class ChangePasswordAsyncTask extends AsyncTask<String, Void, String> {
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();
        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private ProgressDialog dialog =
                new ProgressDialog(ChangePasswordActivity.this);

        protected void onPreExecute() {
            dialog.setMessage("Getting your data... Please wait...");
            dialog.show();
        }

        protected String doInBackground(String... urls) {

            String URL = null;

            try {

                URL = urls[0];
                HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);

                HttpPost httpPost = new HttpPost(URL);

             /*   //add name value pair for the country code
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("start",String.valueOf(start)));
                nameValuePairs.add(new BasicNameValuePair("limit",String.valueOf(limit)));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));*/
                response = httpclient.execute(httpPost);

                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();
                } else {
                    //Closes the connection.
                    Log.w("HTTP1:", statusLine.getReasonPhrase());
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                Log.w("HTTP2:", e);
                content = e.getMessage();
                error = true;
                cancel(true);
            } catch (IOException e) {
                Log.w("HTTP3:", e);
                content = e.getMessage();
                error = true;
                cancel(true);
            } catch (Exception e) {
                Log.w("HTTP4:", e);
                content = e.getMessage();
                error = true;
                cancel(true);
            }

            return content;
        }

        protected void onCancelled() {
            dialog.dismiss();
            Toast toast = Toast.makeText(ChangePasswordActivity.this,
                    "Error connecting to Server", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 600);
            View view1 = toast.getView();
            view1.setBackgroundResource(R.drawable.toast_drawablecolor);
            toast.show();

        }

        protected void onPostExecute(String content) {
            dialog.dismiss();
            Toast toast;
            if (error) {
                toast = Toast.makeText(ChangePasswordActivity.this,
                        content, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 600);
                toast.show();
            } else {
                displayCountryList(content);


            }
        }


    }

    private void displayCountryList(String response) {
        if (response != null) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                String rs_code = jsonObject.getString("RSCode");
                if (rs_code.equals("1")) {
                    Toast toast = Toast.makeText(ChangePasswordActivity.this,
                            "Successfully Password Changed ", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 600);
                    View view1 = toast.getView();
                    view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast.show();
                    DbHandler.dbHandler.deleteUserProfile();
                    startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
                    finish();

                }
                else {
                    Toast toast = Toast.makeText(ChangePasswordActivity.this,
                            "Incorrect data", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 600);
                    View view1 = toast.getView();
                    view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Toast toast = Toast.makeText(ChangePasswordActivity.this,
                    "incorrect Old Password", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 600);
            View view1 = toast.getView();
            view1.setBackgroundResource(R.drawable.toast_drawablecolor);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
          switch (id){
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
