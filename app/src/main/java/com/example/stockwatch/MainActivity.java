package com.example.stockwatch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StockAdapter mAdapter;
    private HashMap<String, String> wCompanies = new HashMap<>();
    private String sStockSearched = "";
    private String TAG = "MAINACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        asyncLoadCompanies();

    }

    private void asyncLoadCompanies(){
        new AsyncLoaderCompanyNames(this).execute();
    }

    public void updateData(HashMap<String, String> wData) {
//        if (aData.isEmpty()) {
//            Toast.makeText(this, "Please Enter a Valid City Name", Toast.LENGTH_SHORT).show();
//            return;
//        }
        Log.d(TAG, "updateData: " + wData.size());

        wCompanies = new HashMap<String, String>(wData);

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
                createStockDialogBox();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createStockDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stock Selection");
        builder.setMessage("Please enter a Stock Symbol");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        input.setTextSize(24);
        input.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sStockSearched = input.getText().toString();
                HashMap aMatchingCompanies = aSymbolsAndNames(sStockSearched);
                Log.d(TAG, "onClick: size " + aMatchingCompanies);
                if (aMatchingCompanies.size() > 1){
                    //if more than one company fits the search then display selection
                    createSelectorDialogBox(aMatchingCompanies);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void createSelectorDialogBox(HashMap<String, String> aMatchingCompanies){
        //for each item in hashmap - concatenate symbol and company name, create string list
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");
// add a list
        String[] animals = {"horse", "cow", "camel", "sheep", "goat"};
        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // horse
                    case 1: // cow
                    case 2: // camel
                    case 3: // sheep
                    case 4: // goat
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
