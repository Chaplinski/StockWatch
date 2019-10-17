
package com.example.stockwatch;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // DB Name
    private static final String DATABASE_NAME = "StockAppDB";

    // DB Table Name
    private static final String TABLE_NAME = "StockWatchTable";

    ///DB Columns
    private static final String STOCKSYMBOL = "StockSymbol";
    private static final String COMPANYNAME = "CompanyName";

    // DB Table Create Code
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    STOCKSYMBOL + " TEXT not null unique," +
                    COMPANYNAME + " INT not null)";

    private SQLiteDatabase database;


    DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase(); // Inherited from SQLiteOpenHelper
        Log.d(TAG, "DatabaseHandler: C'tor DONE");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // onCreate is only called is the DB does not exist
        Log.d(TAG, "onCreate: Making New DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public ArrayList<String[]> loadStocks() {

        // Load countries - return ArrayList of loaded countries
        Log.d(TAG, "loadCountries: START");
        ArrayList<String[]> stocks = new ArrayList<>();

        Cursor cursor = database.query(
                TABLE_NAME,  // The table to query
                new String[]{STOCKSYMBOL, COMPANYNAME}, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                STOCKSYMBOL); // The sort order

        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                stocks.add(new String[]{symbol, company});
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadCountries: DONE");

        return stocks;
    }

    void addStock(Stock stock) {
        ContentValues values = new ContentValues();

        values.put(STOCKSYMBOL, stock.getSymbol());
        values.put(COMPANYNAME, stock.getCompany());

        database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addStock: Add Complete");
        Log.d(TAG, "addStock: " + stock.getSymbol());
        Log.d(TAG, "addStock: " + stock.getCompany());
    }

    void updateStock(Stock stock) {
        ContentValues values = new ContentValues();

        values.put(STOCKSYMBOL, stock.getSymbol());
        values.put(COMPANYNAME, stock.getCompany());

        database.update(TABLE_NAME, values, STOCKSYMBOL + " = ?", new String[]{stock.getSymbol()});

        Log.d(TAG, "updateStock: Update Complete");
    }

    void deleteStock(String symbol) {
        Log.d(TAG, "deleteStock: Deleting Stock" + symbol);

        int cnt = database.delete(TABLE_NAME, STOCKSYMBOL + " = ?", new String[]{symbol});

        Log.d(TAG, "deleteCountry: " + cnt);
    }

    void dumpDbToLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Log.d(TAG, "dumpDbToLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
            for (int i = 0; i < cursor.getCount(); i++) {
                String sSymbol = cursor.getString(0);
                String sName = cursor.getString(1);
                Log.d(TAG, "dumpDbToLog: " +
                        String.format("%s %-18s", STOCKSYMBOL + ":", sSymbol) +
                        String.format("%s %-18s", COMPANYNAME + ":", sName));
                cursor.moveToNext();
            }
            cursor.close();
        }

        Log.d(TAG, "dumpDbToLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }

    void shutDown() {
        database.close();
    }
}
