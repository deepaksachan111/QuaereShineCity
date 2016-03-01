package com.quaere.deepak.quaereshinecity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * Created by deepak sachan on 8/26/2015.
 */
public class HttpAgent extends AppCompatActivity{

    private static final String TAG = HttpAgent.class.getSimpleName();


    private HttpAgent() {
    }

    /**
     * makes a get request to a url
     *
     * @param url URL
     * @return String response from the url
     */
    public static final String get(String url) {
        String response = null;
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        try {
            HttpGet httpGet = new HttpGet(url);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpClient.execute(httpGet, responseHandler);
        } catch (Exception e) {
            Log.e(TAG, "HttpClient Exception:" + e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        Log.d(TAG, "response from api on get: " + response + ", url=" + url);
        return response;
    }

    /**
     * makes a post request to a url with some data
     *
     * @param url    String url
     * @param params List of name:value pairs to sent with the request as post data
     * @return String response of the post request
     */
    public static final String post(String url) {
        String response = null;
        Log.e(TAG,"sending post reqeust={}, params={}");
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        try {
            HttpPost httpPost = new HttpPost(url);
         // httpPost.setEntity(new UrlEncodedFormEntity(params));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpClient.execute(httpPost, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();




        } finally {httpClient.getConnectionManager().shutdown();

        }
        Log.d(TAG,"response from api on post: " + response + ", url=" + url);
        return response;
    }



}
