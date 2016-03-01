package com.quaere.deepak.quaereshinecity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.quaere.deepak.quaereshinecity.Db.DbHandler;
import com.quaere.deepak.quaereshinecity.TodayTransactionActivity;
import com.quaere.deepak.quaereshinecity.Adapter.StatementAdapter;
import com.quaere.deepak.quaereshinecity.DatewiseTransactionActivity;
import com.quaere.deepak.quaereshinecity.DebitTransactionActivity;
import com.quaere.deepak.quaereshinecity.R;
import com.quaere.deepak.quaereshinecity.SearchTransactionActivity;

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


public class StatementFragment extends android.support.v4.app.Fragment {
 private String TOTAL;
    private String CREDITTOTAL;
    int position;
    ListView list;
    String[] itemname = {
            "Today's Transaction", "Datewise Transaction", "Debit Transaction", "Credit Transaction", "Search Privilege Card- Invoice No."
    };

    Integer[] imgid = {
            R.drawable.today_transaction,
            R.drawable.datewise_transaction,
            R.drawable.arrow_up,
            R.drawable.arrow_down,
            R.drawable.search
    };

    public StatementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbHandler.startIfNotStarted(getActivity());
        String venderid = DbHandler.dbHandler.getuserProfileList().getVenderid();
        String urlstotal ="http://demo8.mlmsoftindia.com/shinepanel.svc/TodayTotal/"+ venderid;
        new TodayTotalAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, urlstotal);
        String urlcreditstotal ="http://demo8.mlmsoftindia.com/shinepanel.svc/CreditTotal/"+ venderid;
        new CreditTransAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, urlcreditstotal);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_statement, container, false);
        StatementAdapter adapter = new StatementAdapter(getActivity().getApplicationContext(), R.layout.mystatementadapter, itemname, imgid);
        list = (ListView) view.findViewById(R.id.listv_statement);
        list.setAdapter(adapter);
       list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               switch (position) {
                   case 0:
                      Intent i=new Intent(getActivity(),TodayTransactionActivity.class);
                       i.putExtra("T",TOTAL);
                       startActivity(i);
                      // getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new AccountFragment()).addToBackStack(null).commit();
                       break;
                   case 1:
                       getActivity().startActivity(new Intent(getActivity(),DatewiseTransactionActivity.class));
                       break;
                   case 2:
                       getActivity().startActivity(new Intent(getActivity(),DebitTransactionActivity.class));
                       break;
                   case 3:
                       Intent intent=new Intent(getActivity(),CreditTransactionActivity.class);
                       intent.putExtra("TT",CREDITTOTAL);
                       startActivity(intent);
                       break;
                   case 4:
                       getActivity().startActivity(new Intent(getActivity(),SearchTransactionActivity.class));
                       break;
               }
           }
       });

        return view;
    }


    private class TodayTotalAsync extends AsyncTask<String,Void,String> {
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();
        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content =  null;
        private boolean error = false;
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
            // dialog.dismiss();
            Toast toast = Toast.makeText(getActivity(),
                    "Error connecting to Server", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            View view1 = toast.getView();
            view1.setBackgroundResource(R.drawable.toast_drawablecolor);
            toast.show();

        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null){
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        TOTAL =   jsonObject.getString("Balance");
                       // String tttt = TOTAL;

                    }
                } catch (JSONException e) {


                    e.printStackTrace();
                }
            }
        }
    }


    private class CreditTransAsync extends AsyncTask<String,Void,String> {
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();
        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content =  null;
        private boolean error = false;
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
            // dialog.dismiss();
            Toast toast = Toast.makeText(getActivity(),
                    "Error connecting to Server", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();

        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null){
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                       CREDITTOTAL =   jsonObject.getString("Balance");
                        // String tttt = TOTAL;
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }
    }

}
