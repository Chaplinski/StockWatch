package com.example.stockwatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private static final String TAG ="StockAdapter";
    private List<Stock> stockList;
    private MainActivity mainAct;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private boolean bHasInternet;
    private ArrayList<String[]> aDBLoadedStocks;


    public StockAdapter(List<Stock> stockList, MainActivity ma, boolean bNetCheck, ArrayList<String[]> aDBLoadedStocks) {
        this.stockList = stockList;
        mainAct = ma;
        bHasInternet = bNetCheck;
        this.aDBLoadedStocks = aDBLoadedStocks;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType){
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_list_row, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {

        Stock stock = stockList.get(position);

        if (bHasInternet){
//            Log.d(TAG, "onBindViewHolder: symbol - " + stock.getSymbol());
//            Log.d(TAG, "onBindViewHolder: company - " + stock.getCompany());
//            Log.d(TAG, "onBindViewHolder: price - " + stock.getCurrentPrice());
//            Log.d(TAG, "onBindViewHolder: percent change - " + stock.getPercentChange());
//            Log.d(TAG, "onBindViewHolder: price change - " + stock.getPriceChange());
            holder.sSymbol.setText(stock.getSymbol());
            holder.sCompany.setText(stock.getCompany());
            double dPrice = stock.getCurrentPrice();
            String sPrice = Double.toString(dPrice);
            holder.sCurrentPrice.setText(sPrice);
            //get price and percent change and concatenate them
            double dPriceChange = stock.getPriceChange();
            String sPriceChange = Double.toString(dPriceChange);
            double dPercentChange = stock.getPercentChange() * 100;
            String sPercentChange = df2.format(dPercentChange);

            if ((int)sPriceChange.charAt(0) == '-'){
                String sCollectiveChange = "▼" + sPriceChange +" (" + sPercentChange + "%)";
                holder.sCollectiveChange.setText(sCollectiveChange);
                holder.sSymbol.setTextColor(RED);
                holder.sCompany.setTextColor(RED);
                holder.sCurrentPrice.setTextColor(RED);
                holder.sCollectiveChange.setTextColor(RED);
            } else {
                String sCollectiveChange = "▲" + sPriceChange +" (" + sPercentChange + "%)";
                holder.sCollectiveChange.setText(sCollectiveChange);
                holder.sSymbol.setTextColor(GREEN);
                holder.sCompany.setTextColor(GREEN);
                holder.sCurrentPrice.setTextColor(GREEN);
                holder.sCollectiveChange.setTextColor(GREEN);
            }
        }
        else {
            String[] aStockInfo = aDBLoadedStocks.get(position);
            holder.sSymbol.setText(aStockInfo[0]);
            holder.sCompany.setText(aStockInfo[1]);
            holder.sCurrentPrice.setText("0.0");
            holder.sCollectiveChange.setText("0.0 (0.0%)");
        }

    }


    public void removeItem(int position) {
        stockList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount(){ return stockList.size(); }

}
