package com.quaere.deepak.quaereshinecity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.quaere.deepak.quaereshinecity.Db.DbHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RedeemProceedActivity extends AppCompatActivity {
    private TextView pricardno, invoice, amoun;
    private EditText passcode;
    private Button proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));
        setContentView(R.layout.activity_redeem_proceed);
        findviewbyid();
        Intent i = getIntent();
        final String pricard = i.getStringExtra("PRI");
        final String inv = i.getStringExtra("IN");
        final String amo = i.getStringExtra("AM");
        DbHandler.startIfNotStarted(this);
      final   String venderid = DbHandler.dbHandler.getuserProfileList().getVenderid();
        pricardno.setText(pricard);
        invoice.setText(inv);
        amoun.setText(amo);
        proceed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String Passcode = passcode.getText().toString();
            new PassCodeAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,venderid,pricard,inv,amo,Passcode);
            }
        });

    }

    class PassCodeAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RedeemProceedActivity.this, "Please Wait", "Connecting", true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String url = "http://demo8.mlmsoftindia.com/ShinePanel.svc/Redeem/"+params[0]+"/"+params[1]+"/"+params[2]+"/"+params[3]+"/"+params[4];
            String response = HttpAgent.post(url);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(s != null){
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsonObject = (JSONObject)jsonArray.get(0);
                    String rs_code= jsonObject.getString("RSCode");
                    if(rs_code.equals("1")){
                        startActivity(new Intent(RedeemProceedActivity.this,RedeemSuccessActivity.class));
                    }else{
                        Toast toast=  Toast.makeText(RedeemProceedActivity.this, "invalid passcode", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 25, 500);
                        View view1 = toast.getView();
                        view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                   Toast toast= Toast.makeText(RedeemProceedActivity.this, "invoiceno. already access for day", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 500);
                    View view1 = toast.getView();
                    view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast.show();
                }

            }
        }

    }

    public void findviewbyid() {
        pricardno = (TextView) findViewById(R.id.txt_privi);
        invoice = (TextView) findViewById(R.id.txt_invoice);
        amoun = (TextView) findViewById(R.id.txt_amount);
        passcode = (EditText) findViewById(R.id.edt_passcode);
        proceed = (Button) findViewById(R.id.bt_proceed);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_redeem_proceed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
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
