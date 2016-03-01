package com.quaere.deepak.quaereshinecity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cete.dynamicpdf.Document;
import com.cete.dynamicpdf.Page;
import com.cete.dynamicpdf.PageOrientation;
import com.cete.dynamicpdf.PageSize;
import com.cete.dynamicpdf.TextAlign;
import com.cete.dynamicpdf.pageelements.Label;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.CFFFont;
import com.lowagie.text.pdf.PdfWriter;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BankDetailActivity extends AppCompatActivity {
         private TextView bankname,accountno,accountype,branchname,ifsccode,otherbranch;
    String AccountNo; String AccountType; String BankName; String BranchName;String IFSCCode;String OtherBranch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'  size:'2'>Bank Detail</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88236c")));
        setContentView(R.layout.activity_bank_detail);
        bankname = (TextView)findViewById(R.id.bank_name);
        accountno = (TextView)findViewById(R.id.account_number);
        accountype = (TextView)findViewById(R.id.account_type);
        branchname = (TextView)findViewById(R.id.branch_name);
        ifsccode = (TextView)findViewById(R.id.ifsc_code);
        otherbranch = (TextView)findViewById(R.id.other_branch);

        DbHandler.startIfNotStarted(this);
        String venderid = DbHandler.dbHandler.getuserProfileList().getVenderid();
        String url = "http://demo8.mlmsoftindia.com/ShinePanel.svc/BankProfile/"+ venderid;
       // String url ="http://demo8.mlmsoftindia.com/ShinePanel.svc/CancelledBooking/tnabi786";
        new  BankDetailAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);
      Button b= (Button)findViewById(R.id.button1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String s =  BankName +"-"+ AccountNo +"-"+ AccountType+"-"+BranchName + "-"+ IFSCCode +"-"+ OtherBranch;
                createPDF(s);

            }
        });
    }
private class BankDetailAsyncTask extends AsyncTask<String, Void, String>{
    ProgressDialog dialog = new ProgressDialog(BankDetailActivity.this);
    private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    private final HttpClient httpclient = new DefaultHttpClient();
    final HttpParams params = httpclient.getParams();
    HttpResponse response;
    private String content =  null;
    private boolean error = false;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setTitle("Please Wait....");
        dialog.setMessage("Connecting");
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
        Toast toast = Toast.makeText(BankDetailActivity.this,
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
            toast = Toast.makeText(BankDetailActivity.this,
                    content, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 600);
            View view1 = toast.getView();
            view1.setBackgroundResource(R.drawable.toast_drawablecolor);
            toast.show();
        } else {
            displayCountryList(content);
           // dialog.dismiss();
        }
    }

}

    private void displayCountryList(String response){

        try {
            JSONArray jArray = new JSONArray(response);

            for (int i = 0; i < jArray.length(); i++)
            {

                JSONObject jObj = jArray.getJSONObject(i);
                AccountNo = jObj.getString("AccountNo");
                AccountType = jObj.getString("AccountType");
                BankName = jObj.getString("BankName");
                BranchName = jObj.getString("BranchName");
                IFSCCode = jObj.getString("IFSCCode");
                OtherBranch = jObj.getString("OtherBranch");

                bankname.setText(BankName);
                accountno.setText(AccountNo);
                accountype.setText(AccountType);
                branchname.setText(BranchName);
                ifsccode.setText(IFSCCode);
                otherbranch.setText(OtherBranch);
            }


        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void createPDF(String s)
    {
      /*  String FILE = Environment.getExternalStorageDirectory()
                + "/HelloWorld.pdf";

        // Create a document and set it's properties
        com.cete.dynamicpdf.Document objDocument = new com.cete.dynamicpdf.Document();
        objDocument.setCreator("DynamicPDFHelloWorld.java");
        objDocument.setAuthor("Your Name");
        objDocument.setTitle("Hello World");

        // Create a page to add to the document
        Page objPage = new Page(PageSize.LETTER, PageOrientation.PORTRAIT,
                54.0f);

        // Create a Label to add to the page
       String strText = "Hello World...\nFrom DynamicPDF Generator "
                + "for Java\nDynamicPDF.com";
        Label objLabel = new Label(s, 0, 0, 504, 100,
                com.cete.dynamicpdf.Font.getHelvetica(), 18, TextAlign.CENTER);

        // Add label to page
        objPage.getElements().add(objLabel);

        // Add page to document
        objDocument.getPages().add(objPage);


        try {
            // Outputs the document to file
            objDocument.dString FILE = Environment.getExternalStorageDirectory()
                    + "/HelloWorld.pdf";raw(FILE);
            Toast.makeText(this, "File has been written to :" + FILE,
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this,
                    "Error, unable to write to file\n" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }*/
        String string = s;
        String[] parts = string.split("-");
        String part1 = parts[0];
        String part2 = parts[1];
        String part3 = parts[2];
        String part4 = parts[3];
        String part5 = parts[4];
        String part6 = parts[5];
        com.lowagie.text.Document doc = new com.lowagie.text.Document();
        String FILE = Environment.getExternalStorageDirectory()
                + "/HelloWorld.pdf";

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/droidText";

            File dir = new File(path);
            if(!dir.exists())
                dir.mkdirs();

            Log.d("PDFCreator", "PDF Path: " + path);


            File file = new File(dir, "sample.pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.contact);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Image myImg = Image.getInstance(stream.toByteArray());


            //add image to document
            doc.add(myImg);
            Paragraph p1 = new Paragraph("Bank Name:      " + part1);
            Font paraFont= new Font(Font.COURIER,20.0f,Color.GREEN);
            p1.setAlignment(Paragraph.ALIGN_LEFT);
            p1.setFont(paraFont);

            //add paragraph to document
            doc.add(p1);

            Paragraph p2 = new Paragraph("Account No:    " + part2);
            Font paraFont2= new Font(Font.COURIER,20.0f,Color.GREEN);
            p2.setAlignment(Paragraph.ALIGN_LEFT);
            p2.setFont(paraFont2);
            doc.add(p2);

            Paragraph p3 = new Paragraph("Account Type:    " + part3);
            Font paraFont3= new Font(Font.COURIER,20.0f,Color.GREEN);
            p3.setAlignment(Paragraph.ALIGN_LEFT);
            p3.setFont(paraFont3);
            doc.add(p3);

            Paragraph p4 = new Paragraph("Branch name:    " + part4);
            Font paraFont4= new Font(Font.COURIER,20.0f,Color.GREEN);
            p4.setAlignment(Paragraph.ALIGN_LEFT);
            p4.setFont(paraFont4);
            doc.add(p4);

            Paragraph p5 = new Paragraph("IFSC Code:    " + part5);
            Font paraFont5= new Font(Font.COURIER,20.0f,Color.GREEN);
            p5.setAlignment(Paragraph.ALIGN_LEFT);
            p5.setFont(paraFont5);
            doc.add(p5);

            Paragraph p6 = new Paragraph("Other Branch:    " + part6);
            Font paraFont6= new Font(Font.COURIER,20.0f,Color.GREEN);
            p6.setAlignment(Paragraph.ALIGN_LEFT);
            p6.setFont(paraFont6);
            doc.add(p6);
            //set footer
            Phrase footerText = new Phrase("This is an example of a footer");
            HeaderFooter pdfFooter = new HeaderFooter(footerText, false);
            doc.setFooter(pdfFooter);

            Toast.makeText(this, "File has been written to :" + path, Toast.LENGTH_LONG).show();


        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        }  finally
        {

            doc.close();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bank_detail, menu);
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
