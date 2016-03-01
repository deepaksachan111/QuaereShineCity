package com.quaere.deepak.quaereshinecity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

/**
 * Created by deepak sachan on 9/1/2015.
 */
public class HHttpPost {


        private static final String TAG = HttpAgent.class.getSimpleName();
        private static final String API_FAT_GAI = "apiFatGai";
        private static final String UNAUTHENTICATED = "unauthenticated";



        private HHttpPost() {
        }




        public static String post(String url, List<NameValuePair> params, Activity activity) {
            Log.i(TAG, "making POST request to : " + url + "with params" + params);
            String data = postImplementation(url, params, activity);
            Log.i(TAG, "response to request " + url + " is " + data);

            return data;
        }




        private static String postImplementation(String url, List<NameValuePair> params, Activity activity) {
            String response = null;
            Log.i(TAG, "sending post reqeust = " + url + ", params = " + params);
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            HttpClient httpClient = new DefaultHttpClient(httpParams);
            try {
                org.apache.http.client.methods.HttpPost httpPost = new org.apache.http.client.methods.HttpPost(url);
                  httpPost.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,PUT");
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
              // httpPost.setEntity(new StringEntity(params));
             //  httpPost.setEntity(new StringEntity(params));
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                response = httpClient.execute(httpPost, responseHandler);
            } catch (Exception e) {

                Log.e(TAG, "HttpClient Exception POST : for request " + url, e);
                if (e instanceof org.apache.http.client.HttpResponseException && ((HttpResponseException) e).getStatusCode() == 401) {
                  //  deleteCredentials(activity);
                    return UNAUTHENTICATED;
                } else if (e instanceof org.apache.http.client.HttpResponseException && ((HttpResponseException) e).getStatusCode() == 500) {
                    return API_FAT_GAI;
                }
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            Log.d(TAG, "response from api on post: " + response + ", url=" + url);
            return response;
        }



    public static String getResponse(String url,String params)
    {
       String ss ="";
        //InputStream is = null;
        try
        {

            HttpPost request = new HttpPost(url);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            StringEntity entity = new StringEntity(params);
            request.setEntity(entity);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);

           ss=EntityUtils.toString(response.getEntity());

            Log.v("log", ss);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return ss;
    }

    }

