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
        sSymbol = view.findViewById(R.id.textSymbol);
        sCompany = view.findViewById(R.id.textName);
        sCurrentPrice = view.findViewById(R.id.textPrice);
        sCollectiveChange = view.findViewById(R.id.textChange);




    }


}
