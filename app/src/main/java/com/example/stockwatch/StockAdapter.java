package com.example.stockwatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private static final String TAG ="StockAdapter";
    private List<Stock> stockList;
    private MainActivity mainAct;
    private static DecimalFormat df2 = new DecimalFormat("#.##");


    public StockAdapter(List<Stock> stockList, MainActivity ma) {
        this.stockList = stockList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType){

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_list_row, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position){

        Stock stock = stockList.get(position);
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
        String sCollectiveChange = "▲" + sPriceChange +" (" + sPercentChange + "%)";
        holder.sCollectiveChange.setText(sCollectiveChange);

    }


    public void removeItem(int position) {
        stockList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount(){ return stockList.size(); }

}
