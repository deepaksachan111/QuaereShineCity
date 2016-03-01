package com.quaere.deepak.quaereshinecity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quaere.deepak.quaereshinecity.Db.DbHandler;

public class SearchTransactionActivity extends AppCompatActivity {
  private EditText privilege,invoice;
    private Button search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'  size:'2'>Search Transaction</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));

        setContentView(R.layout.activity_search_transaction);
          privilege = (EditText)findViewById(R.id.edt_search_privcrad);
         invoice =(EditText)findViewById(R.id.edt_search_invoice);
        search = (Button)findViewById(R.id.btn_searchbtton);
        DbHandler.startIfNotStarted(this);


            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   String pri =privilege.getText().toString();
                   String   invo =invoice.getText().toString();
                    if ((!privilege.getText().toString().equals(""))||(! invoice.getText().toString().equals(""))) {

                        if(privilege.getText().toString().equals("")){
                        String card = "CardNo";

                            String venderid = DbHandler.dbHandler.getuserProfileList().getVenderid();
                            String url = "http://demo8.mlmsoftindia.com/shinepanel.svc/SearchTrans/" + venderid + "/" + card + "/" + invo;
                            Intent i = new Intent(SearchTransactionActivity.this,SearchTransactionDataActivity.class);
                            i.putExtra("URL",url);
                            startActivity(i);
                           // new SearchTransactionAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
                        }if (invoice.getText().toString().equals("")){
                            String inv = "InvoiceNo";
                            String venderid = DbHandler.dbHandler.getuserProfileList().getVenderid();
                            String url = "http://demo8.mlmsoftindia.com/shinepanel.svc/SearchTrans/" + venderid + "/" + pri + "/" + inv;
                            Intent i = new Intent(SearchTransactionActivity.this,SearchTransactionDataActivity.class);
                            i.putExtra("URL", url);
                            startActivity(i);
                          //  new SearchTransactionAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
                        } if((!privilege.getText().toString().equals(""))&&(! invoice.getText().toString().equals(""))) {


                            String venderid = DbHandler.dbHandler.getuserProfileList().getVenderid();
                            String url = "http://demo8.mlmsoftindia.com/shinepanel.svc/SearchTrans/" + venderid + "/" + pri + "/" + invo;
                            Intent i = new Intent(SearchTransactionActivity.this,SearchTransactionDataActivity.class);
                            i.putExtra("URL", url);
                            startActivity(i);
                        }
                        }else {
                        Toast toast =Toast.makeText(SearchTransactionActivity.this,"Enter Card No. or invoice no.",Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP,25,500);
                        View view1 = toast.getView();
                        view1.setBackgroundResource(R.drawable.toast_drawablecolor);
                        toast.show();
                    }
                }
            });
        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_transaction, menu);
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
