package com.motorola.leaf.rest;

import com.motorola.leaf.constants.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class RestFulGet
{
    private RestFulGet()
    {

    }

    public static String get(String url)
    {
        try {

            HttpClient httpClient = new DefaultHttpClient(getHttpParams());
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet(url);
            String text;

            // Construct JSON Object from Bean
            httpGet.setHeader("Accept", "application/json");


            HttpResponse response = httpClient.execute(httpGet, localContext);

            HttpEntity entity = response.getEntity();


            text = getASCIIContentFromEntity(entity);

            return text;
        }
        catch (Exception e)
        {
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

    private static String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException
    {
        InputStream in = entity.getContent();

        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n>0)
        {
            byte[] b = new byte[4096];
            n =  in.read(b);


            if (n>0) out.append(new String(b, 0, n));
        }

        return out.toString();
    }
}
