package com.example.stockwatch;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class StockViewHolder extends RecyclerView.ViewHolder {

    public TextView sSymbol;
    public TextView sCompany;
    public TextView sCurrentPrice;
    public TextView sCollectiveChange;

    public StockViewHolder(View view){
        super(view);




    }


}
