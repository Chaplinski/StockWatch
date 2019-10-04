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
                Toast.makeText(this, "ooo lala", Toast.LENGTH_SHORT).show();
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
                //ArrayList<String> listOfKeys = new ArrayList<String>();
                List<String> listOfKeys = getAllKeysForValue(wCompanies, sStockSearched);
                Log.d(TAG, "onClick: size " + listOfKeys.size());
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

    private static <K, V> List<K> getAllKeysForValue(Map<K, V> mapOfWords, V value)
    {
        List<K> listOfKeys = null;

        //Check if Map contains the given value
        if(mapOfWords.containsValue(value))
        {
            // Create an Empty List
            listOfKeys = new ArrayList<>();

            // Iterate over each entry of map using entrySet
            for (Map.Entry<K, V> entry : mapOfWords.entrySet())
            {
                // Check if value matches with given value
                if (entry.getValue().equals(value))
                {
                    // Store the key from entry to the list
                    listOfKeys.add(entry.getKey());
                    Log.d("tag", "getAllKeysForValue: " + entry.getKey());
                }
            }
        }
        // Return the list of keys whose value matches with given value.
        return listOfKeys;
    }
}
