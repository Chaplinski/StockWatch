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

//    @Override
//    protected void onPostExecute(String s) {
//        mainActivity.updateData(wData, bitmap);
//    }

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
                JSONObject weather = jsonArray.getJSONObject(i);
                String foo = weather.getString("symbol");
                Log.d(TAG, "parseJSON: symbol " + foo);
            }

//            Log.d(TAG, "parseJSON: " + jObjMain);

//            JSONArray weather = jObjMain.getJSONArray("weather");
//            JSONObject jWeather = (JSONObject) weather.get(0);
//            wData.put("COND", jWeather.getString("main"));
//            wData.put("DESC", jWeather.getString("description"));
//
//            JSONObject jMain = jObjMain.getJSONObject("main");
//            wData.put("TEMP", jMain.getString("temp"));
//            wData.put("HUMID", jMain.getString("humidity"));
//
//            JSONObject jWind = jObjMain.getJSONObject("wind");
//            wData.put("WIND", jWind.getString("speed"));
//
//            wData.put("CITY", jObjMain.getString("name"));
//
//            JSONObject jSys = jObjMain.getJSONObject("sys");
//            wData.put("COUNTRY", jSys.getString("country"));
//
//            long dt = jObjMain.getLong("dt");
//            String date = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.getDefault()).format(new Date(dt * 1000));
//            wData.put("DATE", date);
//
//            Log.d(TAG, "onPostExecute: " + date);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
