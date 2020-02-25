package net.odessa.odisw.http;

import android.os.AsyncTask;
import android.os.StrictMode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.odessa.odisw.model.Request;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class AsyncHttp extends AsyncTask<Object, Void, Boolean> {

    private Request request;
    private String url;
    private String phone;

    public AsyncHttp(String url, String phone, Request request) {

        if (android.os.Build.VERSION.SDK_INT > 14)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        this.request = request;
        this.phone = phone;
        this.url = url;

    }

    public Boolean execute(){
        try {

            Gson gson = new GsonBuilder()
                    .setExclusionStrategies()
                    .create();
            String json = gson.toJson(request);

            try{
                json = json.replace("\\u003d", "=");
                json = json.replace(",\"isSended\":false", "");
                json = json.replace(",\"isSended\":true", "");
            } catch (Exception e){}

            return execute(url, phone, json).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected Boolean doInBackground(Object... params) {

        try {
            String json = (String) params[2];

            System.out.println(json);

            URL url = new URL(params[0].toString() + "/?f=stat&n=" + params[1].toString());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setConnectTimeout(30000);
            httpURLConnection.setFixedLengthStreamingMode(json.length());
            httpURLConnection.connect();

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(json);
            wr.flush();
            wr.close();

            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                InputStream response = httpURLConnection.getErrorStream();

                System.out.println("RESPONSE CODE: "+responseCode);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response));
                StringBuilder sb = new StringBuilder();
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    System.out.println(sb);
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    try {
                        response.close();
                        return false;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }

            } else {
                InputStream response = httpURLConnection.getInputStream();

                System.out.println("RESPONSE CODE: "+responseCode);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response));
                StringBuilder sb = new StringBuilder();
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    try {
                        response.close();
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}
