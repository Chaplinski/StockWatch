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

public class AsyncLoaderStockValues extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsyncLoaderStockValues";
    @SuppressLint("StaticFieldLeak")
    private String sStockSymbol;
    private MainActivity mainActivity;
    private Stock stock = new Stock();

    private static final String stockURL = " https://cloud.iexapis.com/stable/stock/";
    private static final String yourAPIKey = "pk_8f31f65b562a4dbc9d3dd847757cbf7f";
    //
    //////////////////////////////////////////////////////////////////////////////////


    AsyncLoaderStockValues(MainActivity ma, String sIncomingStockSymbol) {
        mainActivity = ma;
        sStockSymbol = sIncomingStockSymbol;
    }

    @Override
    protected void onPostExecute(String s) {
        mainActivity.updateStockData(stock);
    }

    @Override
    protected String doInBackground(String... params) { // 0 == city, 1 == fshrenheit
        String sLocalURL = stockURL;
        sLocalURL += sStockSymbol + "/quote";
        Uri.Builder buildURL = Uri.parse(sLocalURL).buildUpon();

        buildURL.appendQueryParameter("token", yourAPIKey);
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground2: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground3: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        parseJSON(sb.toString());


        return null;
    }

    private void parseJSON(String s) {

        try {

            JSONObject jObjMain = new JSONObject(s);
            String sSymbol = jObjMain.getString("symbol");
            String sCompanyName = jObjMain.getString("companyName");
            Double dLatestPrice = jObjMain.getDouble("latestPrice");
            Double dChangePrice = jObjMain.getDouble("change");
            Double dChangePercent = jObjMain.getDouble("changePercent");
            stock.setSymbol(sSymbol);
            stock.setCompany(sCompanyName);
            stock.setCurrentPrice(dLatestPrice);
            stock.setPriceChange(dChangePrice);
            stock.setPercentChange(dChangePercent);

        } catch (Exception e) {
            Log.d(TAG, "parseJSON: exception caught");
            e.printStackTrace();
        }
    }
}
