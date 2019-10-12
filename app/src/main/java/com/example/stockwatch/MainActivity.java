package com.example.stockwatch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnLongClickListener, InputDialog.InputDialogListener {

    private RecyclerView recyclerView;
    private StockAdapter mAdapter;
    private HashMap<String, String> wCompanies = new HashMap<>();
    private List<Stock> aStocks = new ArrayList<>();
    private String TAG = "MAINACTIVITY";
    private DatabaseHandler databaseHandler;
    private ArrayList<String[]> aDBLoadedStocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        asyncLoadCompanies();
        databaseHandler = new DatabaseHandler(this);
        aDBLoadedStocks = databaseHandler.loadStocks();
        String[] aStoredStockSymbols = getStoredStockSymbols();
        recyclerView = findViewById(R.id.recycler);
        mAdapter = new StockAdapter(aStocks, this);
        Log.d(TAG, "onCreate4: " + aStocks);
        recyclerView.setAdapter(mAdapter);

        //check for network connection
        if(bNetworkCheck()){
            //Toast.makeText(this, "CONNECTION!", Toast.LENGTH_SHORT).show();
            //for each stock in aDBLoadedStocks execute StockDownloader async task
            //we only need the stock symbol to look up the stock
            for(int i = 0; i < aDBLoadedStocks.size(); i++){
                //execute async stock downloader
                asyncLoadStocks(aStoredStockSymbols[i]);
            }
        } else {
            //here we need aDBLoadedStocks since we do not have internet connection and need both the symbol and company name
            Toast.makeText(this, "NO SIRE", Toast.LENGTH_SHORT).show();
            //show no network error dialog
            //put all stocks on display with price, change, and percent equal to 0
            //sort stock list
            //notify adapter of changed dataset
        }



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
        // use this method to delete a note
//        final TextView name = v.findViewById(R.id.name);
//        final String thisTitle = name.getText().toString();
//        final int position = recyclerView.getChildLayoutPosition(v);

//        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setMessage("Delete note \"" + thisTitle + "\"?")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                       // saveNotes(true, thisTitle);
//                        mAdapter.removeItem(position);
//                    }
//
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //remains empty
//                    }
//
//                })
//                .show();
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
        String[] aStoredStockSymbols = new String [aDBLoadedStocks.size()];
        aDBLoadedStocks = databaseHandler.loadStocks();
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
        wCompanies = new HashMap<String, String>(wData);

    }

    public void updateStockData(Stock oIncomingStock) {
        aStocks.add(oIncomingStock);
        //TODO sort stock list
        //TODO notify adapter of changed dataset
//        recyclerView.setAdapter(mAdapter);
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
                    Toast.makeText(this, "SCOOPY DOOPY DOO", Toast.LENGTH_SHORT).show();
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
        searchHashMap(input);
    }

    public void createStockDialogBox() {
        Log.d(TAG, "createStockDialogBox: ");
        InputDialog inputDialog = new InputDialog();
        inputDialog.show(getSupportFragmentManager(), "example dialog");
    }

    public void searchHashMap(String input){
        HashMap aMatchingCompanies = aSymbolsAndNames(input);
                Log.d(TAG, "onClick: size " + aMatchingCompanies);
                if (aMatchingCompanies.size() > 1){
                    //if more than one company fits the search then display selection
                    createSelectorDialogBox(aMatchingCompanies);
//                    Log.d(TAG, "onClick: foo " + foo);
                }
    }
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Stock Selection");
//        builder.setMessage("Please enter a Stock Symbol");
//
//        // Set up the input
//        final EditText input = new EditText(this);
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        input.setGravity(Gravity.CENTER_HORIZONTAL);
//        input.setTextSize(24);
//        input.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
//        builder.setView(input);
//
//        // Set up the buttons
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                sStockSearched = input.getText().toString();
//                HashMap aMatchingCompanies = aSymbolsAndNames(sStockSearched);
//                Log.d(TAG, "onClick: size " + aMatchingCompanies);
//                if (aMatchingCompanies.size() > 1){
//                    //if more than one company fits the search then display selection
//                    String foo = createSelectorDialogBox(aMatchingCompanies);
//                    Log.d(TAG, "onClick: foo " + foo);
//                }
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();


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
            // do what you have to do here
            // In your case, another loop.
        }
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");
        // add a list
        builder.setItems(aCompanies, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: which");
                Stock stock = new Stock();
                stock.setSymbol(aSymbols[which]);
                stock.setCompany(aNames[which]);
                databaseHandler.addStock(stock);
//TODO
                databaseHandler.dumpDbToLog();

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
