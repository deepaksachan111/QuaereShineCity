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
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class DatewiseTransactionsShowDataActivity extends AppCompatActivity {
   private ArrayList<Datewisedata> datewisedata;
    private DatewiseArrayAdapter datewiseArrayAdapter;
  private  String total;
    private MenuItem item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String fromDate = intent.getStringExtra("FROMDATE");
        String toDate = intent.getStringExtra("TODATE");
        String type = intent.getStringExtra("TYPE");

       // total =intent.getStringExtra("TOTAL");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(type +" Transaction");

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));
        setContentView(R.layout.activity_datewise_transactions_show_data);
        DbHandler.startIfNotStarted(this);
         String venderid = DbHandler.dbHandler.getuserProfileList().getVenderid();
        new DatewiseTotalTransactionAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, venderid, fromDate, toDate, type);
         new DatewiseTranDataAsynkTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,venderid,fromDate,toDate,type);



    }

    class DatewiseTranDataAsynkTask extends AsyncTask<String,Void,String>{
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();
        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content =  null;
        private boolean error = false;
        ProgressDialog dialog = new ProgressDialog(DatewiseTransactionsShowDataActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Getting your data... Please wait...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... param) {
            String url ="http://demo8.mlmsoftindia.com/shinepanel.svc/MonthlyTrans/"+ param[0]+"/"+param[1]+ "/"+param[2]+"/"+param[3];

            try {

              String  URL = url;
                HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);

                HttpPost httpPost = new HttpPost(URL);

               //add name value pair for the country code
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("start",String.valueOf(param[2])));
                nameValuePairs.add(new BasicNameValuePair("limit", String.valueOf(param[3])));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
               // content = URLDecoder.decode(URL, "UTF-8");
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            Toast toast;
            if (s.contains("[]")) {
                toast = Toast.makeText(DatewiseTransactionsShowDataActivity.this,
                        "Data Is Empty", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 500);
                View view1 = toast.getView();
                view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast.show();
            }else if(s.contains("Connect to /114.129.32.14:80 timed out")){
                toast = Toast.makeText(DatewiseTransactionsShowDataActivity.this,
                        "Server TimeOut", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 500);
                View view1 = toast.getView();
                view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast.show();
            }else {
                displayCountryList(s);
            }
        }
    }

    private void displayCountryList(String response){
        String narration; String date; String amount; String type;
        try {
            JSONArray jArray = new JSONArray(response);
            datewisedata = new ArrayList<Datewisedata>();
            for (int i = 0; i < jArray.length(); i++)
            {

                JSONObject jsonObject = jArray.getJSONObject(i);
                narration = jsonObject.getString("Narration");
                date = jsonObject.getString("Date");
                amount = jsonObject.getString("Amount");
                type = jsonObject.getString("Type");
                datewisedata.add(new Datewisedata(narration,date,amount,type));
            }

            datewiseArrayAdapter = new DatewiseArrayAdapter(this, R.layout.debittranscation_arryadapter, datewisedata);
            ListView listView = (ListView) findViewById(R.id.listview_datewisetransaction);
            listView.setAdapter(datewiseArrayAdapter);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public class DatewiseArrayAdapter extends ArrayAdapter<Datewisedata> {
        private ArrayList<Datewisedata> datewisedata;
        private Context context;
        // private TextView Date,Month,Year,Amount;


        public DatewiseArrayAdapter(Context context, int resource,ArrayList<Datewisedata> datewisedatas) {
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
                convertView = vi.inflate(R.layout.datewisetransactionadapter, null);

                holder = new ViewHolder();
                holder.data = (TextView) convertView.findViewById(R.id.txt_datewisedata);
                holder.time = (TextView) convertView.findViewById(R.id.txt_datewisedate);
                holder.Amount = (TextView) convertView.findViewById(R.id.txt_datewiseamount);
                holder.type = (TextView)convertView.findViewById(R.id.txt_datewisetype);

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
    private class DatewiseTotalTransactionAsyncTask extends AsyncTask<String,Void,String> {
        ProgressDialog dialog = new ProgressDialog(DatewiseTransactionsShowDataActivity.this);
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();
        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content =  null;
        private boolean error = false;
        @Override
        protected String doInBackground(String... urls) {
            String url ="http://demo8.mlmsoftindia.com/shinepanel.svc/MonthlyTotal/"+ urls[0]+"/"+urls[1]+ "/"+urls[2]+"/"+urls[3];
            String URL = null;

            try {

                URL = url;
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null){
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        total =   jsonObject.getString("Balance");
                       // String tttt = TOTAL;
                        /*SpannableString s2 = new SpannableString(total);
                        s2.setSpan(new ForegroundColorSpan(Color.parseColor("#88236c")), 0, s2.length(), 0);*/
                        item.setTitle(total);
                    }
                } catch (JSONException e) {


                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_datewise_transactions_show_data, menu);
        int positionOfMenuItem = 0;
        item = menu.getItem(positionOfMenuItem);
         if(total == null){
             SpannableString s = new SpannableString("");
             s.setSpan(new ForegroundColorSpan(Color.parseColor("#88236c")), 0, s.length(), 0);
             item.setTitle(s);
             return true;
         }else {
             SpannableString s = new SpannableString(total);
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
         switch(id){
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
