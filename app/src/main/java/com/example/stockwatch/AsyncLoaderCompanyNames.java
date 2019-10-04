package com.example.stockwatch;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AsyncLoaderCompanyNames extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsyncLoaderTask";
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private HashMap<String, String> wData = new HashMap<>();

    private static final String companiesURL = "https://api.iextrading.com/1.0/ref-data/symbols";

    AsyncLoaderCompanyNames(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected void onPostExecute(String s) {
        mainActivity.updateData(wData);
    }

    @Override
    protected String doInBackground(String... params) { // 0 == city, 1 == fshrenheit

        Log.d(TAG, "doInBackground: " + companiesURL);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(companiesURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        parseJSON(sb.toString());


        return null;
    }

    private void parseJSON(String s) {

        try {
            JSONArray jsonArray = new JSONArray(s);
//            JSONObject jObjMain = new JSONObject(s);
            Log.d(TAG, "parseJSON: length " + jsonArray.length());

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject oCompanies = jsonArray.getJSONObject(i);
                wData.put(oCompanies.getString("symbol"), oCompanies.getString("name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
