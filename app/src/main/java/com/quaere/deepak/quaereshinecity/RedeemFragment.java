package com.quaere.deepak.quaereshinecity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.quaere.deepak.quaereshinecity.Db.DbHandler;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;


public class RedeemFragment extends android.support.v4.app.Fragment {

    public static final String TAG = RedeemFragment.class.getSimpleName();
    private EditText searchPrivCard, invoiceno;
    private EditText amount;
    private TextView txtpriBalance;
    private Button proceed;
    private String data;

    ProgressDialog progressDialog;

    public RedeemFragment() {
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
        View view = inflater.inflate(R.layout.fragment_redeem, container, false);
        searchPrivCard = (EditText) view.findViewById(R.id.edt_privilegecard_no);
        txtpriBalance = (TextView) view.findViewById(R.id.txt_balance);
        invoiceno = (EditText) view.findViewById(R.id.edt_invoice_no);

        amount = (EditText) view.findViewById(R.id.edt_amount);
        amount.setFilters(new InputFilter[]
                {new InputFilterMinMax("1", "100000000")});

        proceed = (Button) view.findViewById(R.id.proceed_btn);
        proceed.setVisibility(View.GONE);

        DbHandler.startIfNotStarted(getActivity());


        searchPrivCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = searchPrivCard.getText().toString();

                if (text.length() == 16) {
                    progressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Connecting ...", true);
                    progressDialog.setCancelable(true);
                    String url = "http://demo8.mlmsoftindia.com/ShinePanel.svc/CardBalance/";
                    new RedeemAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, text);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        return view;
    }


    public class RedeemAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String toadd = params[0] + params[1];
            String result = HttpAgent.get(toadd);


            return result;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                JSONParser parser = new JSONParser();
                try {
                    Object obj = parser.parse(response);
                    JSONArray jsonArray = (JSONArray) obj;
                    org.json.simple.JSONObject object1 = (org.json.simple.JSONObject) jsonArray.get(0);
                    data = (String) object1.get("CardAmount");
                    final String rscode = (String) object1.get("RSCode");
                    if (rscode.equals("1")) {
                        txtpriBalance.setText(data.toString());
                        progressDialog.dismiss();
                        proceed.setVisibility(View.VISIBLE);
                    } else {
                        progressDialog.dismiss();
                        Toast toast = Toast.makeText(getActivity(), "invalid card no", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 25, 400);
                        toast.show();
                    }
                    proceed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String pricardno = searchPrivCard.getText().toString();

                            String invoice = invoiceno.getText().toString();

                            String amountw = amount.getText().toString();
                            String balance1 = DbHandler.dbHandler.getuserProfileList().getBalance();
                            String cardbalance=data.toString();

                            Double obj1 = Double.parseDouble(amountw);
                            Double obj2 =Double.parseDouble(balance1);
                            Double obj3 = Double.parseDouble(cardbalance);
                           // double check = obj1.compareTo(obj2);
                            double check2 = obj1.compareTo(obj3);
                            if( obj1 > obj2 ) {
                             Toast toast =   Toast.makeText(getActivity(), "Insufficent vender balance", Toast.LENGTH_LONG);
                                View view1 = toast.getView();
                                view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                                toast.setGravity(Gravity.TOP,25,500);
                                amount.setText("");
                                amount.requestFocus();
                                amount .setFocusableInTouchMode(true);

                              toast.show();
                                if(obj1 > obj3 ){
                                    Toast toast2 =   Toast.makeText(getActivity(), "Insufficent card balance", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP,25,500);
                                    View view2 = toast.getView();
                                    view2.setBackgroundResource(R.drawable.toast_drawablecolor);
                                    toast.show();
                                    amount.setText("");
                            }
                            }else {
                                amount.setText("");
                                if (rscode.equals("0")) {
                                  Toast toast =   Toast.makeText(getActivity(), "Enter Card No.", Toast.LENGTH_LONG);
                                    View view1 = toast.getView();
                                    view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                                    toast.setGravity(Gravity.TOP,25,500);
                                } else if (invoice.equals("") || amountw.equals("")) {
                           /* if (invoice.length() <= 0) {
                                Toast.makeText(getActivity(), "Enter Invoice No.", Toast.LENGTH_LONG).show();
                            }
                            if (amountw.length() <= 0) {
                                Toast.makeText(getActivity(), "Enter Amount", Toast.LENGTH_LONG).show();
                            }*/
                                    Toast toast = Toast.makeText(getActivity(), "Enter invoice no.! amount", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP, 25, 500);
                                    View view1 = toast.getView();
                                    view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                                    toast.show();
                                } else {
                                    Intent intent = new Intent(getActivity(), RedeemProceedActivity.class);
                                    intent.putExtra("PRI", pricardno);
                                    intent.putExtra("IN", invoice);
                                    intent.putExtra("AM", amountw);
                                    startActivity(intent);
                                }
                            }
                        }
                    });
                    Log.v(TAG, "card amount.........." + data);
                } catch (org.json.simple.parser.ParseException e) {
                    e.printStackTrace();
                }
            } else {
                progressDialog.dismiss();
                Toast toast = Toast.makeText(getActivity(), "Invalid Cardno.! Try Another One", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 25, 500);
                View view1 = toast.getView();
                view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                toast.show();

            }
            //  txtpriBalance.setText(s);
            super.onPostExecute(response);
        }
    }

}
