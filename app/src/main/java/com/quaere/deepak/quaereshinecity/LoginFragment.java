package com.quaere.deepak.quaereshinecity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.quaere.deepak.quaereshinecity.Activity.CardLogin;
import com.quaere.deepak.quaereshinecity.Db.DbHandler;
import com.quaere.deepak.quaereshinecity.DbTable.CardProfile;
import com.quaere.deepak.quaereshinecity.DbTable.UserProfile;
import com.quaere.deepak.quaereshinecity.Fragment.ProfileFragment;
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


public class LoginFragment extends android.support.v4.app.Fragment {

    public static final String TAG = LoginFragment.class.getSimpleName();
    private TextView forgetpass;
    private Button loginbutton;
    private EditText edname;
    private EditText edpassword;
    public String response;
    String Displayname;
    String MemberID;
    String loginid;
    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                DbHandler.startIfNotStarted(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        edname = (EditText) view.findViewById(R.id.edt_login);
        edpassword = (EditText) view.findViewById(R.id.edt_password);
        loginbutton = (Button) view.findViewById(R.id.login_btn);
        forgetpass = (TextView) view.findViewById(R.id.txt_forget);
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //getActivity().startActivity(new Intent(getActivity(), ForgetPasswordActivity.class));
                forgetPasswordDialog();
            }
        });

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHandler.startIfNotStarted(getActivity());
                String userName = edname.getText().toString();
                edname.setText("");
                String password = edpassword.getText().toString();
                edpassword.setText("");
                if (userName.length() == 0 || password.length() == 0) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Enter Username and password", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 25, 500);
                    View view1 = toast.getView();
                    view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast.show();
                    return;
                }
                if (userName.length() == 16) {


                    String url = "http://demo8.mlmsoftindia.com/ShinePanel.svc/CardLogin/" + userName + "/" + password;
                    Log.v(TAG, "ghhhhhhhhhk" + url);
                    new CardLoginAsynkTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);


                } else if (CheckNetwork.isInternetAvailable(getActivity().getApplication())) //returns true if internet available
                {
                    String url = "http://demo8.mlmsoftindia.com/ShinePanel.svc/VendorLogin/" + userName + "/" + password;
                    Log.v(TAG, "ghhhhhhhhhk" + url);
                    new UserLoginTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);

                    // startActivity(new Intent(getActivity(), PagerslidingActivity.class));

                } else {
                    getFragmentManager().beginTransaction().replace(R.id.fragment, new NoConnectionFragment()).commit();
                    Toast toast = Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 500);
                    View view1 = toast.getView();
                    view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast.show();
                }

            }

        });


        return view;
    }

    public void forgetPasswordDialog() {
//       final String url = "http://demo8.mlmsoftindia.com/ShinePanel.svc/MemberPasswordChange/"+id;

        final EditText loinid, mobileno;
        Button okbtn;
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle(Html.fromHtml("<font color='#FF7F27'>Set IP Address</font>"));
       // dialog.getWindow().setBackgroundDrawableResource(R.color.blue);
        dialog.setContentView(R.layout.forgetpassworddialog);

       // dialog.setTitle("Shine City");


        loinid = (EditText) dialog.findViewById(R.id.editTxt_forgetpassloginid_dialog);
        mobileno = (EditText) dialog.findViewById(R.id.editTxt_forgetpassmobileno_dialog);
        okbtn = (Button) dialog.findViewById(R.id.forgetsetPasswordBtn);
        ImageView iv = (ImageView)dialog.findViewById(R.id.close_dialog);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginid = loinid.getText().toString();


               // forgetlogin.setText("");
               String mobilenono = mobileno.getText().toString();
               // forgetmobile.setText("");
                if(!CheckNetwork.isInternetAvailable(getActivity())){
                    Toast toast = Toast.makeText(getActivity(), "internet  unavilable", Toast.LENGTH_LONG);
                    View tostview = toast.getView();
                    tostview.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast.setGravity(Gravity.TOP, 25, 500);
                    toast.show();
                }else {
                    if (loginid.length() > 3 && mobileno.length()==10) {
                        new ForgetpassdialogAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,loginid,mobilenono);
                      /*  ForgetPasswordActivity forgetPasswordActivity = new ForgetPasswordActivity();
                        forgetPasswordActivity.new ForgetAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,loginid,mobilenono);*/
                       // new ForgetAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, loginid, mobileno);
                        dialog.dismiss();
                    } /*else if (loginid.length() == 16 && mobileno.length()==10)
                    {

                    }*/
                    else {
                        Toast toast = Toast.makeText(getActivity(), "enter correct loginid or mobileno.", Toast.LENGTH_LONG);
                        View tostview = toast.getView();
                        tostview.setBackgroundResource(R.drawable.toast_drawablecolor);
                        toast.setGravity(Gravity.TOP, 25, 500);
                        toast.show();
                    }
                }


            }
        });


        dialog.show();

    }
    public class UserLoginTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "Plesase Wait.....", "Connecting", true);
        }

        @Override
        protected String doInBackground(String... params) {
          /*  List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair(Constants.USERNAME, params[0]));
            pairs.add(new BasicNameValuePair(Constants.PASSWORD, params[1]));
            String username= params[0];
            String password = params[1];*/

            //  String url = "http://demo8.mlmsoftindia.com/ShinePanel.svc/Shinecity/TOWJ146/business";
            response = HttpAgent.get(params[0]);
            // AuthenticationResponse authResponse = null;
            String s = response;
            if (response != null) {
               UserProfileActivity.getString(response);


                //  startActivity(new Intent(getActivity(),PagerslidingActivity.class));
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response2) {
            super.onPostExecute(response2);
            progressDialog.dismiss();
            if(response2.equals("[]")) {

              Toast toast =  Toast.makeText(getActivity(), "invalid username or password", Toast.LENGTH_LONG);
                View view1 = toast.getView();
                view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast.show();
            }else if(response2.equals("")) {
               Toast toast1= Toast.makeText(getActivity(), "server problem", Toast.LENGTH_LONG);
                View view1 = toast1.getView();
                view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast1.show();

            }
            else if(response2 != null){
                response = response2.toString();

                DbHandler.startIfNotStarted(getActivity());
                progressDialog.dismiss();
                startActivity(new Intent(getActivity(), PagerslidingActivity.class));
                getActivity().finish();
            }
            }

        }


    @Override
    public void onStart() {
        UserProfileActivity.BUS.register(getActivity());
        super.onStart();
    }
    private class CardLoginAsynkTask extends AsyncTask<String, Void,String>{

        private static final int REGISTRATION_TIMEOUT = 10 * 1000;
        private static final int WAIT_TIMEOUT = 50 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();
        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content =  null;
        private boolean error = false;
        private ProgressDialog dialog =
                new ProgressDialog(getActivity());

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
            Toast toast = Toast.makeText(getActivity(),
                    "Error connecting to Server", Toast.LENGTH_LONG);
            View tostview = toast.getView();
            tostview.setBackgroundResource(R.drawable.toast_drawablecolor);
            toast.setGravity(Gravity.TOP, 25, 500);
            toast.show();

        }

        protected void onPostExecute(String content) {
            dialog.dismiss();
            Toast toast;
            if (error) {
                toast = Toast.makeText(getActivity(),
                        content, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 500);
                toast.show();
            }   else if (content.equals("[]")) {
                toast = Toast.makeText(getActivity(),
                        "invalid user name or password", Toast.LENGTH_LONG);
                View tostview = toast.getView();
                tostview.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast.setGravity(Gravity.TOP, 25, 500);
                toast.show();
            }else if(content.equals("")) {
              Toast toast1 =  Toast.makeText(getActivity(), "server problem", Toast.LENGTH_LONG);
                View view1 = toast1.getView();
                view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast1.show();
            }
            else if(content !=null) {
                displayCountryList(content);
                Intent intent = new Intent(getActivity(), CardLogin.class);
               /* intent.putExtra("DISPLAYNAME",Displayname);
                intent.putExtra("MEMBERID", MemberID);*/
                startActivity(intent);
                Toast toast2 =Toast.makeText(getActivity().getApplicationContext(), "Welcome  "+ Displayname, Toast.LENGTH_SHORT);
                View tosvie= toast2.getView();
                tosvie.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast2.setGravity(Gravity.TOP, 25, 500);
                toast2.show();
                getActivity().finish();


            }

        }
        private void displayCountryList(String response){

            try {
                JSONArray jArray = new JSONArray(response);

                for (int i = 0; i < jArray.length(); i++)
                {

                    JSONObject jObj = jArray.getJSONObject(i);
                    Displayname = jObj.getString("DisplayName");
                    MemberID = jObj.getString("MemberID");
                    String cardno = jObj.getString("CardNo");
                    String totalbalance = jObj.getString("TotalValue");
                    String redemac = jObj.getString("RedeemValue");
                    String balance = jObj.getString("BalanceValue");

                    DbHandler.dbHandler.saveCardProfile(new CardProfile(cardno,Displayname, totalbalance,redemac,balance,MemberID));

                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    class ForgetpassdialogAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "Please Wait", "Connecting", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String Url = "http://demo8.mlmsoftindia.com/ShinePanel.svc/ForgetPassword/" + params[0] + "/" + params[1];

            String s = HttpAgent.post(Url);
            return s;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            progressDialog.dismiss();
            if (response != null) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject1 = (JSONObject) jsonArray.get(0);
                    String code = jsonObject1.getString("RSCode");
                    if (code.equals("1")) {
                        Intent intent = new Intent(getActivity(),OTPPasswordChangeActivity.class);
                        intent.putExtra("loginname", loginid);
                        startActivity(intent);
                        Toast toast = Toast.makeText(getActivity(), "Send OTP on your mobile.", Toast.LENGTH_LONG);

                        View tostview = toast.getView();
                        tostview.setBackgroundResource(R.drawable.toast_drawablecolor);
                        toast.setGravity(Gravity.TOP, 25, 500);
                        toast.show();
                    } else {
                       Toast toast = Toast.makeText(getActivity(), "invalid loginid or mobileno.", Toast.LENGTH_LONG);
                        View tostview = toast.getView();
                        tostview.setBackgroundResource(R.drawable.toast_drawablecolor);
                        toast.setGravity(Gravity.TOP, 25, 500);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

                Toast toast= Toast.makeText(getActivity(), "invalid loginid or mobileno.", Toast.LENGTH_LONG);
                View tostview = toast.getView();
                tostview.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast.setGravity(Gravity.TOP, 25, 500);
                toast.show();
            }
        }
    }
 /*   class CardForgetpassdialogAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "Please Wait", "Connecting", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String Url = "http://demo8.mlmsoftindia.com/ShinePanel.svc/ForgetPassword/" + params[0] + "/" + params[1];

            String s = HttpAgent.post(Url);
            return s;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            progressDialog.dismiss();
            if (response != null) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject1 = (JSONObject) jsonArray.get(0);
                    String code = jsonObject1.getString("RSCode");
                    if (code.equals("1")) {
                        Intent intent = new Intent(getActivity(),OTPPasswordChangeActivity.class);
                        intent.putExtra("loginname", loginid);
                        startActivity(intent);
                        Toast toast = Toast.makeText(getActivity(), "Send OTP on your mobile.", Toast.LENGTH_LONG);

                        View tostview = toast.getView();
                        tostview.setBackgroundResource(R.drawable.toast_drawablecolor);
                        toast.setGravity(Gravity.TOP, 25, 400);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(getActivity(), "invalid loginid or mobileno.", Toast.LENGTH_LONG);
                        View tostview = toast.getView();
                        tostview.setBackgroundResource(R.drawable.toast_drawablecolor);
                        toast.setGravity(Gravity.TOP, 25, 400);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

                Toast toast= Toast.makeText(getActivity(), "invalid loginid or mobileno.", Toast.LENGTH_LONG);
                View tostview = toast.getView();
                tostview.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
        }
    }*/
}
