package com.quaere.deepak.quaereshinecity.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quaere.deepak.quaereshinecity.Db.DbHandler;
import com.quaere.deepak.quaereshinecity.MainActivity;
import com.quaere.deepak.quaereshinecity.R;

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


public class ChangePasscodeFragment extends Fragment {
    private EditText oldpass, newpasscode, confirmpasscode;
    private Button btn_submit;
     private  View toastview;
    public ChangePasscodeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_change_passcode, container, false);
        oldpass = (EditText) view.findViewById(R.id.edt_oldpasscode);
        newpasscode = (EditText) view.findViewById(R.id.edt_newpasscode);
        confirmpasscode = (EditText) view.findViewById(R.id.edt_confirmpasscode);
        btn_submit = (Button) view.findViewById(R.id.btn_changepasscode_submit);
        DbHandler.startIfNotStarted(getActivity());

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpassword = oldpass.getText().toString();
                String newpass = newpasscode.getText().toString();
                String confirmpass = confirmpasscode.getText().toString();
                if (oldpassword.length() == 0) {
                    Toast toast2 = Toast.makeText(getActivity().getApplicationContext(), "Enter the OldPasscode", Toast.LENGTH_SHORT);
                    View tosvie = toast2.getView();
                    tosvie.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast2.setGravity(Gravity.TOP, 25, 600);
                    toast2.show();
                } else if (newpass.length() == 0) {
                    Toast toast2 = Toast.makeText(getActivity().getApplicationContext(), "Enter the NewPasscode", Toast.LENGTH_SHORT);
                    View tosvie = toast2.getView();
                    tosvie.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast2.setGravity(Gravity.TOP, 25, 600);
                    toast2.show();
                } else if (!newpass.equals(confirmpass)) {
                    Toast toast2 = Toast.makeText(getActivity().getApplicationContext(), "ConfirmPasscode Not Match", Toast.LENGTH_SHORT);
                    View tosvie = toast2.getView();
                    tosvie.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast2.setGravity(Gravity.TOP, 25, 600);
                    toast2.show();
                } else {
                    oldpass.setText("");
                    newpasscode.setText("");
                    confirmpasscode.setText("");
                    String cardno = DbHandler.dbHandler.getcardProfileList().getCardno();
                    String url = "http://demo8.mlmsoftindia.com/ShinePanel.svc/CardPasscode/"+cardno+"/"+oldpassword+"/"+newpass;
                    new ChangePasscodeAynskTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);
                }
            }
        });
        return view;
    }

    private class ChangePasscodeAynskTask extends AsyncTask<String, Void, String> {
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();
        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private ProgressDialog dialog =
                new ProgressDialog(getActivity());

        protected void onPreExecute() {
            dialog.setMessage("Getting your data... Please wait...");
            dialog.show();

        }
            @Override
            protected String doInBackground (String...urls){
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
            Toast toast = Toast.makeText(getActivity(),
                    "Error connecting to Server", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 600);
            toastview = toast.getView();
            toastview.setBackgroundResource(R.drawable.toast_drawablecolor);
            toast.show();

        }
        protected void onPostExecute(String content) {
            dialog.dismiss();
            Toast toast;
            if (error) {
                toast = Toast.makeText(getActivity(),
                        content, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 600);
                toastview = toast.getView();
                toastview.setBackgroundResource(R.drawable.toast_drawablecolor);
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
                    Toast toast = Toast.makeText(getActivity(),
                            "Successfully Password Changed ", Toast.LENGTH_LONG);
                    toastview = toast.getView();
                    toastview.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast.setGravity(Gravity.TOP, 25, 600);

                    toast.show();
                     getFragmentManager().beginTransaction().replace(R.id.frame_container,new ProfileFragment()).commit();

                   /* DbHandler.dbHandler.deletecardProfile();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();*/

                }
                else if (rs_code.equals("0")){
                    Toast toast = Toast.makeText(getActivity(),
                            "Incorrect data", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 600);
                    toastview = toast.getView();
                    toastview.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Toast toast = Toast.makeText(getActivity(),
                    "incorrect Old Passcode", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 600);
            toastview = toast.getView();
            toastview.setBackgroundResource(R.drawable.toast_drawablecolor);
            toast.show();
        }
    }
}