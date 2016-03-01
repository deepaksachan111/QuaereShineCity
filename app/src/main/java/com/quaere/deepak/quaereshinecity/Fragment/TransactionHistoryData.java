package com.quaere.deepak.quaereshinecity.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.quaere.deepak.quaereshinecity.Db.DbHandler;
import com.quaere.deepak.quaereshinecity.R;
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
import java.util.ArrayList;
import java.util.List;


public class TransactionHistoryData extends Fragment {

    private TransactionHistoryDataArrayAdapter transactionHistoryDataArrayAdapter;
    private ArrayList<Datewisedata> datewisedata;
   private ListView listView;
   private TextView text;
    private  View layout;



    public TransactionHistoryData() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String Fromdate = getArguments().getString("FROMDATE");
        String Todate = getArguments().getString("TODATE");
        View view = inflater.inflate(R.layout.fragment_transaction_history_data, container, false);
        listView = (ListView) view.findViewById(R.id.listview_transactionhistorydata);

         layout = inflater.inflate(R.layout.custom_toast_layout, container, false);

         text = (TextView) layout.findViewById(R.id.textToShow);
        // Set the Text to show in TextView



        DbHandler.startIfNotStarted(getActivity());
        String cardno = DbHandler.dbHandler.getcardProfileList().getCardno();
      new TransactionHistoryDataAsynkTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Fromdate, Todate, cardno);
        return view;
    }


    class TransactionHistoryDataAsynkTask extends AsyncTask<String, Void, String> {
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();
        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Getting your data... Please wait...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... param) {
            String url = "http://demo8.mlmsoftindia.com/shinepanel.svc/CardTransaction/" + param[0] + "/" + param[1] + "/" + param[2];

            try {

                String URL = url;
                HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);

                HttpPost httpPost = new HttpPost(URL);

                //add name value pair for the country code
             /*   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("start", String.valueOf(param[2])));
                nameValuePairs.add(new BasicNameValuePair("limit", String.valueOf(param[3])));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));*/
                // content = URLDecoder.decode(URL, "UTF-8");
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

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();


           Toast toast;
            if (s.contains("[]")) {
             /*   toast = Toast.makeText(getActivity(),
                        "Transaction not found", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);

                View toastView = toast.getView();
                toastView.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast.show();*/
// Inflate the Layout

                text.setText("Transaction not found");
                Toast toast2 = new Toast(getActivity());
                toast2.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast2.setDuration(Toast.LENGTH_SHORT);
                toast2.setView(layout);
                toast2.show();
                getFragmentManager().beginTransaction().replace(R.id.frame_container,new TransactionHistory()).commit();

            } else if (s.contains("Connect to /114.129.32.14:80 timed out")) {
                toast = Toast.makeText(getActivity(),
                        "Server TimeOut", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 600);
                toast.show();
            } else {
                displayCountryList(s);
                super.onPostExecute(s);
            }

        }
        }



    private void displayCountryList(String response) {

        String narration;
        String date;
        String amount;
        String type;
        try {
            JSONArray jArray = new JSONArray(response);
            datewisedata = new ArrayList<Datewisedata>();
            for (int i = 0; i < jArray.length(); i++) {

                JSONObject jsonObject = jArray.getJSONObject(i);
                narration = jsonObject.getString("Narration");
                date = jsonObject.getString("Date");
                amount = jsonObject.getString("Amount");
                type = jsonObject.getString("Type");
                datewisedata.add(new Datewisedata(narration, date, amount, type));
            }

            transactionHistoryDataArrayAdapter = new TransactionHistoryDataArrayAdapter(getActivity(), R.layout.transactionhistrorydataadapter, datewisedata);

            listView.setAdapter(transactionHistoryDataArrayAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public class TransactionHistoryDataArrayAdapter extends ArrayAdapter<Datewisedata> {
        private ArrayList<Datewisedata> datewisedata;
        private Context context;
        // private TextView Date,Month,Year,Amount;


        public TransactionHistoryDataArrayAdapter(Context context, int resource, ArrayList<Datewisedata> datewisedatas) {
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

                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.transactionhistrorydataadapter, null);

                holder = new ViewHolder();
                holder.data = (TextView) convertView.findViewById(R.id.txt_transhistorydata);
                holder.time = (TextView) convertView.findViewById(R.id.txt_transhistorydate);
                holder.Amount = (TextView) convertView.findViewById(R.id.txt_transhistoryamount);
              //  holder.type = (TextView) convertView.findViewById(R.id.txt_transhistorytype);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Datewisedata dateData = datewisedata.get(position);
            holder.data.setText(dateData.getData());
            holder.time.setText(dateData.getTime());
            holder.Amount.setText(dateData.getAmount());
          //  holder.type.setText(dateData.getType());

            return convertView;

        }



        }

    @Override
    public void onDetach() {
        super.onDetach();
      new TransactionHistoryDataAsynkTask().cancel(true);
    }
}
