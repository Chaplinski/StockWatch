package com.example.stockwatch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnLongClickListener, InputDialog.InputDialogListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiper; // The SwipeRefreshLayout

    private StockAdapter mAdapter;
    private HashMap<String, String> wCompanies = new HashMap<>();
    private List<Stock> aStocks = new ArrayList<>();
    private String TAG = "MAINACTIVITY";
    private DatabaseHandler databaseHandler;
    private ArrayList<String[]> aDBLoadedStocks;
    private String[] aStoredStockSymbols;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        asyncLoadCompanies();
        databaseHandler = new DatabaseHandler(this);
        aDBLoadedStocks = databaseHandler.loadStocks();
        aStoredStockSymbols = getStoredStockSymbols();
        recyclerView = findViewById(R.id.recycler);
        mAdapter = new StockAdapter(aStocks, this, bNetworkCheck(), aDBLoadedStocks);
        Log.d(TAG, "onCreate4: " + aStocks);
        recyclerView.setAdapter(mAdapter);

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        //check for network connection
        if(bNetworkCheck()){
//            Toast.makeText(this, "CONNECTION!", Toast.LENGTH_SHORT).show();
//            for each stock in aDBLoadedStocks execute StockDownloader async task
//            we only need the stock symbol to look up the stock
            getInfoForDBStocks();
        } else {
            //here we need aDBLoadedStocks since we do not have internet connection and need both the symbol and company name
            //show no network error dialog
            createGeneralDialogBox("No Network Connection", "You are not connected to the internet", 0);
            //put all stocks on display with price, change, and percent equal to 0
            getInfoForDBStocks();
            //sort stock list
            //notify adapter of changed dataset
        }



    }

    private void getInfoForDBStocks(){
        for(int i = 0; i < aDBLoadedStocks.size(); i++){
            //execute async stock downloader
            asyncLoadStocks(aStoredStockSymbols[i]);
        }
    }

    private void doRefresh() {
        //clear out aStoredStockSymbols or stocks added this session will disappear on reload
        aStoredStockSymbols = getStoredStockSymbols();
        if(bNetworkCheck()) {
            Log.d(TAG, "doRefresh: " + aStoredStockSymbols.length);
            //clear out the aStocks array or else every stock will appear double on swype
            aStocks.clear();
            getInfoForDBStocks();
            swiper.setRefreshing(false);
            sortStockList();
            mAdapter.notifyDataSetChanged();
        } else {
            swiper.setRefreshing(false);
            createGeneralDialogBox("No Network Connection", "Stocks Cannot Be Updated Without A Network Connection", 0);
        }
    }

    public void sortStockList(){

        Collections.sort(aStocks, new Comparator<Stock>() {
            public int compare(Stock s1, Stock s2) {
                return s1.getSymbol().compareTo(s2.getSymbol());
            }
        });

    }

    @Override
    public void onClick(View v) {  // click listener called by ViewHolder clicks
        TextView thisSymbol = v.findViewById(R.id.textSymbol);
        String sThisSymbol = thisSymbol.getText().toString();
        String sURL = "http://www.marketwatch.com/investing/stock/" + sThisSymbol;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sURL));
        startActivity(browserIntent);
    }

    // From OnLongClickListener
    @Override
    public boolean onLongClick(View v) {  // long click listener called by ViewHolder long clicks
//         use this method to delete a stock
        final TextView textSymbol = v.findViewById(R.id.textSymbol);
        final String thisSymbol = textSymbol.getText().toString();
        final int position = recyclerView.getChildLayoutPosition(v);
//        Toast.makeText(this, thisSymbol, Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete Stock");
        builder.setMessage("Delete Stock Symbol " + thisSymbol + "?");
        builder.setIcon(R.drawable.trashcan);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                databaseHandler.deleteStock(thisSymbol);

                mAdapter.removeItem(position);
                //TODO
                aDBLoadedStocks.remove(thisSymbol);
                aStoredStockSymbols = getStoredStockSymbols();
                //aStoredStockSymbols
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

        return false;
    }



    private boolean bNetworkCheck(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private String[] getStoredStockSymbols(){
        aDBLoadedStocks = databaseHandler.loadStocks();
        String[] aStoredStockSymbols = new String [aDBLoadedStocks.size()];
        for(int i = 0; i < aDBLoadedStocks.size(); i++){
            String[] aRow = aDBLoadedStocks.get(i);
            aStoredStockSymbols[i] = aRow[0];

        }
        return aStoredStockSymbols;
    }

    private void asyncLoadCompanies(){
        new AsyncLoaderCompanyNames(this).execute();
    }

    private void asyncLoadStocks(String sStockSymbol){
        new AsyncLoaderStockValues(this, sStockSymbol).execute();

    }

    public void updateCompanyData(HashMap<String, String> wData) {
        wCompanies = new HashMap<>(wData);

    }

    public void updateStockData(Stock oIncomingStock) {
        aStocks.add(oIncomingStock);
        Log.d(TAG, "updateStockData: symbol - " + oIncomingStock.getSymbol());

        recyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
        sortStockList();


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d(TAG, "updateStockData: " + aStocks.size());

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAddStock:
                if (bNetworkCheck()) {
                    createStockDialogBox();
                    return true;
                } else {
                    createGeneralDialogBox("No Network Connection", "Stocks Cannot Be Added Without a Network Connection", 0);
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void applyTexts(String input) {
        //String input is where the value from the input is sent to the main activity
        Log.d(TAG, "applyTexts: " + input);
        //pass input value to be searched in the hashmap
        //search DB for input value
        if(stockAlreadyExists(input)){
            String sMessage = "Stock Symbol " + input + " is already displayed";
            createGeneralDialogBox("Duplicate Stock", sMessage, 1);
        } else {
            searchHashMap(input);
        }
    }

    public boolean stockAlreadyExists( String sSearchedStock){
        for (int i=0; i < aDBLoadedStocks.size(); i ++){
            String[] aThisStock = aDBLoadedStocks.get(i);
            if(aThisStock[0].equals(sSearchedStock)){
                return true;
            }
        }
        return false;
    }

    public void createStockDialogBox() {
        Log.d(TAG, "createStockDialogBox: ");
        InputDialog inputDialog = new InputDialog();
        inputDialog.show(getSupportFragmentManager(), "Input Dialog");
    }

    public void createGeneralDialogBox(String title, String message, int icon) {
        GeneralDialog generalDialog = new GeneralDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", message);
        bundle.putInt("icon", icon);
        generalDialog.setArguments(bundle);
        generalDialog.show(getSupportFragmentManager(), "No Network Dialog");
    }

    public void searchHashMap(String input){
        HashMap aMatchingCompanies = aSymbolsAndNames(input);
                Log.d(TAG, "onClick: size " + aMatchingCompanies);
                if (aMatchingCompanies.size() > 1){
                    //if more than one company fits the search then display selection
                    createSelectorDialogBox(aMatchingCompanies);
//                    Log.d(TAG, "onClick: foo " + foo);
                } else if (aMatchingCompanies.size() == 1){
                    Stock stock = new Stock();
                    stock.setSymbol(input);
                    stock.setCompany(aMatchingCompanies.get(input).toString());
                    databaseHandler.addStock(stock);
                    asyncLoadStocks(input);
                    mAdapter.notifyDataSetChanged();
                    aStoredStockSymbols = getStoredStockSymbols();
                    sortStockList();

                } else {
                    String sDialogTitle = "Symbol Not Found: " + input;
                    createGeneralDialogBox(sDialogTitle, "Data for stock symbol", 0);
                }
    }

    private String createSelectorDialogBox(HashMap<String, String> aMatchingCompanies){
        //for each item in hashmap - concatenate symbol and company name, create string list
        final String[] aCompanies = new String[aMatchingCompanies.size()];
        final String[] aSymbols = new String[aMatchingCompanies.size()];
        final String[] aNames = new String[aMatchingCompanies.size()];
        int i = 0;
        for(Map.Entry<String, String> entry : aMatchingCompanies.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String sConcatenated = key + " - " + value;
            aSymbols[i] = key;
            aNames[i] = value;
            aCompanies[i] = sConcatenated;
            i++;
        }
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");
        // add a list
        builder.setItems(aCompanies, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sThisSymbol = aSymbols[which];
                if(!Arrays.asList(aStoredStockSymbols).contains(sThisSymbol)) {
                    Log.d(TAG, "onClick: which");
                    Stock stock = new Stock();
                    stock.setSymbol(sThisSymbol);
                    stock.setCompany(aNames[which]);
                    databaseHandler.addStock(stock);
                    databaseHandler.dumpDbToLog();
                    aStoredStockSymbols = getStoredStockSymbols();
                    asyncLoadStocks(stock.getSymbol());
                } else {
                    String sMessage = "Stock Symbol " + sThisSymbol + " is already displayed";
                    createGeneralDialogBox("Duplicate Stock", sMessage, 1);
                }

            }
        });
        // create and show the alert dialog
        builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        return null;

    }

    private HashMap<String, String> aSymbolsAndNames(String sStockSearched){
        HashMap aMatchingCompanies = new HashMap<String, String>();
        //String sCompanyName = wCompanies.get(sStockSearched);

        for(Map.Entry<String, String> entry : wCompanies.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            Log.d(TAG, "aSymbolsAndNames: " + key);
            if(key.contains(sStockSearched) || value.contains(sStockSearched)) {
                aMatchingCompanies.put(key, value);
            }
        }
        return aMatchingCompanies;
    }

}
