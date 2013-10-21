package com.motorola.leaf.rest;

import android.util.Log;

import com.motorola.leaf.constants.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class RestFulPost
{
    private RestFulPost()
    {

    }

    public static String post(String url, Map map)
    {
        HttpClient httpClient = new DefaultHttpClient(getHttpParams());
        HttpResponse response;

        try
        {
            HttpPost httpPost = new HttpPost(url);

            // Construct JSON Object from Bean
            JSONObject jsonData = new JSONObject(map);
            StringEntity stringEntity = new StringEntity(jsonData.toString());
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");

            //httppost.setEntity(new UrlEncodedFormEntity(data));
            response = httpClient.execute(httpPost);
            return inputStreamToString(response.getEntity().getContent());
        }
        catch(Exception e)
        {
            Log.e("RestFulPost", e.getLocalizedMessage(), e);
            return null;
        }
    }

    private static HttpParams getHttpParams()
    {
        HttpParams httpParams = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
        HttpConnectionParams.setSoTimeout(httpParams, 30000);

        return httpParams;
    }

    private static String inputStreamToString(InputStream is) {

        String line;
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        try
        {
            while ((line = rd.readLine()) != null)
            {
                total.append(line);
            }
        }
        catch (IOException e)
        {
            Log.e("RestFulPost", e.getLocalizedMessage(), e);
            return null;
        }

        return total.toString();
    }
}
