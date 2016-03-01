package com.quaere.deepak.quaereshinecity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.quaere.deepak.quaereshinecity.Db.DbHandler;
import com.quaere.deepak.quaereshinecity.model.Datewisedata;

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

public class TodayTransactionActivity extends AppCompatActivity {


    private  ArrayList<Datewisedata> datewisedatas;
    private  TodayTransArrayAdapter todayTransArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'  size:'2'>Today Transaction</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));
        setContentView(R.layout.activity_today_taransaction);
        DbHandler.startIfNotStarted(this);
        String venderid = DbHandler.dbHandler.getuserProfileList().getVenderid();
        String urls="http://demo8.mlmsoftindia.com/shinepanel.svc/TodayTrans/"+ venderid;


        new TodayAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urls);
    }

    private class TodayAsyncTask extends AsyncTask<String,Void,String>{
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();
        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content =  null;
        private boolean error = false;
        private ProgressDialog dialog =
                new ProgressDialog(TodayTransactionActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Getting your data... Please wait...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            String URL = null;

            try {

                URL = urls[0];
                HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);

                HttpPost httpPost = new HttpPost(URL);

                response = httpclient.execute(httpPost);

                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();
                } else{
                    //Closes the connection.
                    Log.w("HTTP1:", statusLine.getReasonPhrase());
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                Log.w("HTTP2:",e );
                content = e.getMessage();
                error = true;
                cancel(true);
            } catch (IOException e) {
                Log.w("HTTP3:",e );
                content = e.getMessage();
                error = true;
                cancel(true);
            }catch (Exception e) {
                Log.w("HTTP4:",e );
                content = e.getMessage();
                error = true;
                cancel(true);
            }

            return content;
        }
        protected void onCancelled() {
            dialog.dismiss();
            Toast toast = Toast.makeText(TodayTransactionActivity.this,
                    "Error connecting to Server", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 500);
            View view1 = toast.getView();
            view1.setBackgroundResource(R.drawable.toast_drawablecolor);
            toast.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            super.onPostExecute(s);
            dialog.dismiss();
            Toast toast;
            if (error) {
                toast = Toast.makeText(TodayTransactionActivity.this,
                        content, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 500);
                View view1 = toast.getView();
                view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast.show();
            } else {
                displaylist(content);
            }
        }
    }

    private void displaylist(String response){

        if(response.equals("[]")){
            Toast toast = Toast.makeText(TodayTransactionActivity.this,"No Records Founds..",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP,25,500);
            View view1 = toast.getView();
            view1.setBackgroundResource(R.drawable.toast_drawablecolor);
            toast.show();
        }else {
            String narration;
            String date;
            String amount;
            String type;
            try {
                JSONArray jArray = new JSONArray(response);
                datewisedatas = new ArrayList<Datewisedata>();
                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject jsonObject = jArray.getJSONObject(i);
                    narration = jsonObject.getString("Narration");
                    date = jsonObject.getString("Date");
                    amount = jsonObject.getString("Amount");
                    type = jsonObject.getString("Type");
                    datewisedatas.add(new Datewisedata(narration, date, amount, type));
                }

                todayTransArrayAdapter = new TodayTransArrayAdapter(this, R.layout.todaytransactionarrayadapter, datewisedatas);
                ListView listView = (ListView) findViewById(R.id.listview_todaytransaction);
                listView.setAdapter(todayTransArrayAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public class TodayTransArrayAdapter extends ArrayAdapter<Datewisedata> {
        private ArrayList<Datewisedata> datewisedata;
        private Context context;
        // private TextView Date,Month,Year,Amount;


        public TodayTransArrayAdapter(Context context, int resource,ArrayList<Datewisedata> datewisedatas) {
            super(context, resource, datewisedatas);
            this.datewisedata = datewisedatas;
            this.context = context;
        }
        private class ViewHolder {
            TextView data;
            TextView time;
            TextView Amount;
            TextView type;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
          /*  ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = layoutInflater.inflate(R.layout.debittranscation_arryadapter, parent, false);*/

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));
            if (convertView == null) {

                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.todaytransactionarrayadapter, null);

                holder = new ViewHolder();
                holder.data = (TextView) convertView.findViewById(R.id.txt_todaynarration);
                holder.time = (TextView) convertView.findViewById(R.id.txt_today_date);
                holder.Amount = (TextView) convertView.findViewById(R.id.txt_todayamount);
                holder.type = (TextView)convertView.findViewById(R.id.txt_today_type);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Datewisedata dateData = datewisedata.get(position);
            holder.data.setText(dateData.getData());
            holder.time.setText(dateData.getTime());
            holder.Amount.setText(dateData.getAmount());
            holder.type.setText(dateData.getType());

            return convertView;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_today_taransaction, menu);
        int positionOfMenuItem = 0;
        Intent i= getIntent();
        String T = i.getStringExtra("T");
        MenuItem item = menu.getItem(positionOfMenuItem);
        if(T == null){
            SpannableString s = new SpannableString("");
            s.setSpan(new ForegroundColorSpan(Color.parseColor("#88236c")), 0, s.length(), 0);
            item.setTitle(s);
            return true;
        }else {
            SpannableString s = new SpannableString(T);
            s.setSpan(new ForegroundColorSpan(Color.parseColor("#88236c")), 0, s.length(), 0);
            item.setTitle(s);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
         if(id == android.R.id.home){
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
