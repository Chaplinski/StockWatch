package com.example.stockwatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private static final String TAG ="StockAdapter";
    private List<Stock> stockList;
    private MainActivity mainAct;

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType){

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_list_row, parent, false);


        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position){

        Stock stock = stockList.get(position);
        holder.sSymbol.setText(stock.getSymbol());
        holder.sCompany.setText(stock.getCompany());
        double dPrice = stock.getCurrentPrice();
        String sPrice = Double.toString(dPrice);
        holder.dCurrentPrice.setText(sPrice);
        double dChange = stock.getChangeToday();
        String sChange = Double.toString(dChange);
        holder.dChangeToday.setText(sChange);

    }

    @Override
    public int getItemCount(){ return stockList.size(); }

}
