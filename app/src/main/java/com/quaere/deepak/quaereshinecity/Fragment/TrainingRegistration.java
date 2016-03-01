package com.quaere.deepak.quaereshinecity.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.quaere.deepak.quaereshinecity.Db.DbHandler;
import com.quaere.deepak.quaereshinecity.R;
import com.quaere.deepak.quaereshinecity.model.Datewisedata;
import com.quaere.deepak.quaereshinecity.model.TrainingData;

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


public class TrainingRegistration extends Fragment {

    private Spinner mySpinner;
    ArrayList<String> dropdwonlist;
    ArrayList<TrainingData> trainingDatas;
    private String trainingidlist;
    private EditText edtmembername;
    private EditText edtmobileno;
    private int pos;

    private Button btnregister;

    private TextView toasttext;
    private View toastlayout;

    public TrainingRegistration() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training_registration, container, false);

        toastlayout = inflater.inflate(R.layout.custom_toast_layout, container, false);

        toasttext = (TextView) toastlayout.findViewById(R.id.textToShow);


        mySpinner = (Spinner) view.findViewById(R.id.my_spinner);
        dropdwonlist = new ArrayList<String>();
        dropdwonlist.add("Select One");
        mySpinner.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                dropdwonlist));
        new TrainingRegistrationAsynkTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        btnregister = (Button) view.findViewById(R.id.btn_register);
        edtmembername = (EditText) view.findViewById(R.id.edt_membername);
        edtmobileno = (EditText) view.findViewById(R.id.edt_membermobile_no);

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String traninid = trainingidlist;
                String memname = edtmembername.getText().toString();
                String mobilen0 = edtmobileno.getText().toString();
                if (memname.length() < 4) {
                    Toast toast;
                    toast = Toast.makeText(getActivity(),
                            "Enter The Valid Name", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 400);

                    View toastView = toast.getView();
                    toastView.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast.show();

                } else if (mobilen0.length() < 10) {
                    Toast toast;
                    toast = Toast.makeText(getActivity(),
                            "Enter The Valid Mobile no.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 400);

                    View toastView = toast.getView();
                    toastView.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast.show();
                } else {
                    DbHandler.start(getActivity());
                    String memid = DbHandler.dbHandler.getcardProfileList().getMemberid();
                    new TrainingRegistrationRegistration().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, traninid, memid, memname, mobilen0);

                }
            }
        });

        return view;
    }

    private class TrainingRegistrationAsynkTask extends AsyncTask<String, Void, String> {
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

        }

        @Override
        protected String doInBackground(String... param) {
            String url = "http://demo8.mlmsoftindia.com/ShinePanel.svc/GetTrainingList";

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
            super.onPostExecute(s);


            Toast toast;
            if (s.contains("[]")) {
                toast = Toast.makeText(getActivity(),
                        "Transaction not found", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);

                View toastView = toast.getView();
                toastView.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast.show();
// Inflate the Layout


            } else if (s.contains("Connect to /114.129.32.14:80 timed out")) {
                toast = Toast.makeText(getActivity(),
                        "Server TimeOut", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            } else {
                displayCountryList(s);

            }
        }
    }

    private void displayCountryList(String response) {
        dropdwonlist.clear();
        dropdwonlist = new ArrayList<String>();
        trainingDatas = new ArrayList<>();
        String narration;
        String trainingid;


        try {
            JSONArray jArray = new JSONArray(response);

            for (int i = 0; i < jArray.length(); i++) {

                JSONObject jsonObject = jArray.getJSONObject(i);
                narration = jsonObject.getString("TrainingName");
                trainingid = jsonObject.getString("TrainingID");
                dropdwonlist.add(narration);
                trainingDatas.add(new TrainingData(narration, trainingid));

            }


            mySpinner.setAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_dropdown_item,
                    dropdwonlist));
            String dataitext = mySpinner.getSelectedItem().toString();

            mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TrainingData td = trainingDatas.get(position);
                    trainingidlist = td.getTrainingid();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class TrainingRegistrationRegistration extends AsyncTask<String, Void, String> {
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();
        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected String doInBackground(String... param) {
            String url = "http://demo8.mlmsoftindia.com/ShinePanel.svc/SaveTraining/" + param[0] + "/" + param[1] + "/" + param[2] + "/" + param[3];

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
            super.onPostExecute(s);


            Toast toast;
            if (s.contains("[]")) {
                toast = Toast.makeText(getActivity(),
                        "Transaction not found", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);

                View toastView = toast.getView();
                toastView.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast.show();
// Inflate the Layout


            } else if (s.contains("Connect to /114.129.32.14:80 timed out")) {
                toast = Toast.makeText(getActivity(),
                        "Server TimeOut", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            } else {
                register(s);

            }
        }


    }

    private void register(String s) {
        try {
            JSONArray jArray = new JSONArray(s);

            for (int i = 0; i < jArray.length(); i++) {

                JSONObject jsonObject = jArray.getJSONObject(i);
                String code = jsonObject.getString("RSCode");
                String response = jsonObject.getString("RSMgs");
                if (code != null) {
                    toasttext.setText(response);
                    Toast toast2 = new Toast(getActivity());
                    toast2.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast2.setDuration(Toast.LENGTH_LONG);
                    toast2.setView(toastlayout);
                    toast2.show();
                    edtmobileno.setText("");
                    edtmembername.setText("");

//Inflate the fragment

                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}