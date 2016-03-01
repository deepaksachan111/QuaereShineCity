package com.quaere.deepak.quaereshinecity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class DebitTransactionActivity extends AppCompatActivity {

    ArrayList<DebitData> debitdatas;
   DebitTransactionArrayAdapter dataAdapter;

    ListView debitdatalistview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));

        setContentView(R.layout.activity_debit_transaction);
       // debitdatalistview = (ListView) findViewById(R.id.listview_debitdata);
        DbHandler.startIfNotStarted(this);
        String venderid = DbHandler.dbHandler.getuserProfileList().getVenderid();
        String url ="http://demo8.mlmsoftindia.com/shinepanel.svc/DebitTrans/"+venderid;
        new DebitTransAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);

    }



    private class DebitTransAsyncTask extends AsyncTask<String, Void, String> {
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();
        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content =  null;
        private boolean error = false;
        private ProgressDialog dialog =
                new ProgressDialog(DebitTransactionActivity.this);

        protected void onPreExecute() {
            dialog.setMessage("Getting your data... Please wait...");
            dialog.setCancelable(true);
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
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();
                } else{
                    //Closes the connection.
                    Log.w("HTTP1:",statusLine.getReasonPhrase());
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
            Toast toast = Toast.makeText(DebitTransactionActivity.this,
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
                toast = Toast.makeText(DebitTransactionActivity.this,
                        content, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 600);
                View view1 = toast.getView();
                view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast.show();
            } else {
                displayCountryList(content);
            }
        }

    }

    private void displayCountryList(String response){

        try {
            JSONArray jArray = new JSONArray(response);
            debitdatas = new ArrayList<DebitData>();
            for (int i = 0; i < jArray.length()-1; i++)
            {
                String day; String month; String year; String amount;
                JSONObject jObj = jArray.getJSONObject(i);
                day = jObj.getString("DayText");
                month = jObj.getString("MonthText");
                year = jObj.getString("YearText");
                amount = jObj.getString("Amount");
                debitdatas.add(new DebitData(amount,day,month,year));
            }

            dataAdapter = new DebitTransactionArrayAdapter(this, R.layout.debittranscation_arryadapter, debitdatas);
            ListView listView = (ListView) findViewById(R.id.listview_debitdata);
            listView.setAdapter(dataAdapter);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public class DebitTransactionArrayAdapter extends ArrayAdapter<DebitData> {
        private ArrayList<DebitData> debitDatas;
        private Context context;
       // private TextView Date,Month,Year,Amount;


        public DebitTransactionArrayAdapter(Context context, int resource,ArrayList<DebitData> debitData) {
            super(context, resource, debitData);
            this.debitDatas = debitData;
            this.context = context;
        }
        private class ViewHolder {
            TextView Date;
            TextView Month;
            TextView Year;
            TextView Amount;
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
                    convertView = vi.inflate(R.layout.debittranscation_arryadapter, null);

                    holder = new ViewHolder();
                    holder.Date = (TextView) convertView.findViewById(R.id.txt_date);
                    holder.Month = (TextView) convertView.findViewById(R.id.txt_month);
                    holder.Year = (TextView) convertView.findViewById(R.id.txt_year);
                    holder.Amount = (TextView) convertView.findViewById(R.id.txt_amount);

                    convertView.setTag(holder);

                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                DebitData debitData = debitdatas.get(position);
                holder.Date.setText(debitData.getDayText());
                holder.Month.setText(debitData.getMonthText());
                holder.Year.setText(debitData.getYearText());
                holder.Amount.setText(debitData.getAmount());

                return convertView;

            }

        }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_debit_transaction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
         switch (id){
             case  android.R.id.home:
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
