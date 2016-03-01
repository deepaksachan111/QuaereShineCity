package com.quaere.deepak.quaereshinecity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.quaere.deepak.quaereshinecity.Db.DbHandler;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DatewiseTransactionActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText fromDateEtxt;
    private EditText toDateEtxt;
    private EditText dropdownETxt;
    private  Button searchbutton;
     private  String FROM;
     private String TO;
     private String TYPE;
     private  String VENDERID;
     private String TOTAL;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    String popUpContents[];
    PopupWindow popupWindowList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'  size:'2'>Datewise Transaction</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));


      /*  ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        TextView txtid = (TextView) mCustomView.findViewById(R.id.id);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));
        txtid.setText("deepak");

        TextView balance = (TextView) mCustomView.findViewById(R.id.balance);
        balance.setText("0.00");*/

        setContentView(R.layout.activity_datewise_transaction);
        fromDateEtxt = (EditText)findViewById(R.id.edt_fromdate);
       // fromDateEtxt.setInputType(InputType.TYPE_NULL);
        toDateEtxt = (EditText) findViewById(R.id.edt_todate);
       // toDateEtxt.setInputType(InputType.TYPE_NULL);
        dropdownETxt=(EditText)findViewById(R.id.edt_dropdown);
        dropdownETxt.setInputType(InputType.TYPE_NULL);
        searchbutton= (Button)findViewById(R.id.datewise_seach);

        DbHandler.startIfNotStarted(this);
        List<String> transactionlist = new ArrayList<String>();
        transactionlist.add("All::0");
        transactionlist.add("Billing::1");
        transactionlist.add("Transfer::2");
        transactionlist.add("Discount Credit::3");
        popUpContents = new String[transactionlist.size()];
        transactionlist.toArray(popUpContents);

		/*
		 * initialize pop up window
		 */
        popupWindowList = popupWindowListMethod();

       // dropdownETxt.setCompoundDrawables(null, null, getResources().getDrawable(R.drawable.contact), null);
         dropdownETxt.setCompoundDrawablesWithIntrinsicBounds(null, null,
                 getResources().getDrawable(R.drawable.dropdown), null);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
             setDateTimeField();


        View.OnClickListener handler = new View.OnClickListener() {
            public void onClick(View v) {

                switch (v.getId()) {

                    case R.id.edt_dropdown:
                        // show the list view as dropdown
                        popupWindowList.showAsDropDown(v, -4, 0);
                        break;
                }
            }
        };

        // our button

        dropdownETxt.setOnClickListener(handler);


        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromDateEtxt.getText().toString().equals("")||toDateEtxt.getText().toString().equals("")){
                    Toast toast = Toast.makeText(DatewiseTransactionActivity.this,"Enter The Date or TransactionType",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP,25,500);
                    View view1 = toast.getView();
                    view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                    toast.show();

                }else {
                    if(dropdownETxt.getText().toString().equals("")){

                        TYPE ="All";
                    } if(dropdownETxt.getText().toString().equals("Discount Credit")){

                        TYPE ="Discount";
                    }
                    VENDERID = DbHandler.dbHandler.getuserProfileList().getVenderid();
                    FROM = fromDateEtxt.getText().toString();
                    TO = toDateEtxt.getText().toString();

                //  new DatewiseTotalTransactionAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,VENDERID,FROM,TO,TYPE);
                    Intent intent = new Intent(DatewiseTransactionActivity.this, DatewiseTransactionsShowDataActivity.class);
                    intent.putExtra("FROMDATE", FROM);
                    intent.putExtra("TODATE",TO);
                    intent.putExtra("TYPE", TYPE);
                    intent.putExtra("TOTAL", TOTAL);
                    startActivity(intent);
                }
            }
        });
    }
    public PopupWindow popupWindowListMethod() {

        // initialize a pop up window type
        final PopupWindow popupWindow = new PopupWindow(this);

        // the drop down list is a list view
        ListView listViewType = new ListView(this);

        // set our adapter and pass our pop up window contents
        listViewType.setAdapter(TypeAdapter(popUpContents));

        // set the item click listener
        listViewType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Context mContext = view.getContext();
                DatewiseTransactionActivity mainActivity = ((DatewiseTransactionActivity) mContext);
                // add some animation when a list item was clicked
                Animation fadeInAnimation =  AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_in);
                fadeInAnimation.setDuration(5);
                view.startAnimation(fadeInAnimation);


                String selectedItemText = ((TextView) view).getText().toString();
                dropdownETxt.setText(selectedItemText);
                VENDERID = DbHandler.dbHandler.getuserProfileList().getVenderid();
                FROM = fromDateEtxt.getText().toString();
                TO = toDateEtxt.getText().toString();
                TYPE = dropdownETxt.getText().toString();
               // new DatewiseTotalTransactionAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, VENDERID, FROM, TO, TYPE);
                popupWindow.dismiss();

                // get the id
                /*String selectedItemTag = ((TextView) view).getTag().toString();
                Toast.makeText(DatewiseTransactionActivity.this, "Dog ID is: " + selectedItemTag, Toast.LENGTH_SHORT).show();*/
            }
        });

        // some other visual settings

        popupWindow.setFocusable(true);

        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the list view as pop up window content
        popupWindow.setContentView(listViewType);

        return popupWindow;
    }
    @Override
    public void onClick(View v) {
        if(v == fromDateEtxt) {
            fromDatePickerDialog.show();
        } else if(v == toDateEtxt) {
            toDatePickerDialog.show();
        }
    }
    private void setDateTimeField() {
        fromDateEtxt.setOnClickListener(this);
        toDateEtxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }
    private ArrayAdapter<String> TypeAdapter(String typeArray[]) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, typeArray)

        {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // setting the ID and text for every items in the list
                String item = getItem(position);
                String[] itemArr = item.split("::");
               String text = itemArr[0];

                String id = itemArr[1];

                // visual settings for the list item
                TextView listItem = new TextView(DatewiseTransactionActivity.this);

                listItem.setText(text);
                listItem.setTag(id);
                listItem.setTextSize(20);
                listItem.setPadding(10, 10, 10, 10);
                listItem.setTextColor(Color.WHITE);

                return listItem;
            }
        };

        return adapter;
    }

   /* private class DatewiseTotalTransactionAsyncTask extends AsyncTask<String,Void,String> {
        ProgressDialog dialog = new ProgressDialog(DatewiseTransactionActivity.this);
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

             *//*   //add name value pair for the country code
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("start",String.valueOf(start)));
                nameValuePairs.add(new BasicNameValuePair("limit",String.valueOf(limit)));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));*//*
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
                            TOTAL =   jsonObject.getString("Balance");
                            String tttt = TOTAL;
                        }
                    } catch (JSONException e) {


                        e.printStackTrace();
                    }
                }
            }
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_datewise_transaction, menu);
        int positionOfMenuItem = 0;
        MenuItem item = menu.getItem(positionOfMenuItem);

            SpannableString s = new SpannableString("");
            s.setSpan(new ForegroundColorSpan(Color.parseColor("#88236c")), 0, s.length(), 0);
            item.setTitle(s);

            return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
          switch (id){
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
