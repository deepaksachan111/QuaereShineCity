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

import com.quaere.deepak.quaereshinecity.model.SearchCrData;

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

public class SearchTransactionDataActivity extends AppCompatActivity {
    private ArrayList<SearchCrData> searchCrDatas;
    private SearchTranCrArrayAdapter searchTranCrArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'  size:'2'>Search Result</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));

        setContentView(R.layout.activity_search_transaction_data);
        Intent i = getIntent();
       String url=  i.getStringExtra("URL");
       // String url1="http://demo8.mlmsoftindia.com/ShinePanel.svc/Direct/tnabi786";
        new SearchTransactionAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }
    private class SearchTransactionAsync extends AsyncTask<String,Void,String> {
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();
        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content =  null;
        private boolean error = false;
        private ProgressDialog dialog =
                new ProgressDialog(SearchTransactionDataActivity.this);
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
            Toast toast = Toast.makeText(SearchTransactionDataActivity.this,
                    "Error connecting to Server", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 500);
            View view1 = toast.getView();
            view1.setBackgroundResource(R.drawable.toast_drawablecolor);
            toast.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            Toast toast;
            if (error) {
                toast = Toast.makeText(SearchTransactionDataActivity.this,
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
            Toast toast = Toast.makeText(SearchTransactionDataActivity.this,"No Records Founds..",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP,25,500);
            View view1 = toast.getView();
            view1.setBackgroundResource(R.drawable.toast_drawablecolor);
            toast.show();
        }

        String narration; String date; String amount; String type;
        try {
            JSONArray jArray = new JSONArray(response);
            searchCrDatas = new ArrayList<SearchCrData>();
            for (int i = 0; i < jArray.length(); i++)
            {

                JSONObject jsonObject = jArray.getJSONObject(i);
                narration = jsonObject.getString("Narration");
                date = jsonObject.getString("Date");
                amount = jsonObject.getString("Amount");
                type = jsonObject.getString("Type");
                searchCrDatas.add(new SearchCrData(narration, date, amount, type));
            }

            searchTranCrArrayAdapter = new SearchTranCrArrayAdapter(this, R.layout.searchtranscrarrayadapter, searchCrDatas);
            ListView listView = (ListView) findViewById(R.id.listview_searchcr_data);
            listView.setAdapter(searchTranCrArrayAdapter);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class SearchTranCrArrayAdapter extends ArrayAdapter<SearchCrData> {
        private ArrayList<SearchCrData> searchCrDatasdata;
        private Context context;
        // private TextView Date,Month,Year,Amount;


        public SearchTranCrArrayAdapter(Context context, int resource, ArrayList<SearchCrData> searchCrDatas) {
            super(context, resource, searchCrDatas);
            this.searchCrDatasdata = searchCrDatas;
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
                convertView = vi.inflate(R.layout.searchtranscrarrayadapter, null);

                holder = new ViewHolder();
                holder.data = (TextView) convertView.findViewById(R.id.txt_serchcrnarration);
                holder.time = (TextView) convertView.findViewById(R.id.txt_searchcr_date);
                holder.Amount = (TextView) convertView.findViewById(R.id.txt_searchcramount);
                holder.type = (TextView)convertView.findViewById(R.id.txt_searchcr_type);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            SearchCrData crData = searchCrDatasdata.get(position);
            holder.data.setText(crData.getNarration());
            holder.time.setText(crData.getDate());
            holder.Amount.setText(crData.getAmount());
            holder.type.setText(crData.getType());

            return convertView;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_transaction_data, menu);
        return true;
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
