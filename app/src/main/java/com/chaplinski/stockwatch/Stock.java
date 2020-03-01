package com.chaplinski.stockwatch;

public class Stock {

    private String sSymbol;
    private String sCompany;
    private double dCurrentPrice;
    private double dPriceChange;
    private double dPercentChange;

    public String getSymbol(){ return sSymbol; }

    public void setSymbol(String newSymbol){ this.sSymbol = newSymbol; }

    public String getCompany(){ return sCompany; }

    public void setCompany(String newCompany){ this.sCompany = newCompany; }

    public double getCurrentPrice(){ return dCurrentPrice; }

    public void setCurrentPrice(double newPrice){ this.dCurrentPrice = newPrice; }

    public double getPriceChange(){ return dPriceChange; }

    public void setPriceChange(double priceChange){ this.dPriceChange = priceChange; }

    public double getPercentChange(){ return dPercentChange; }

    public void setPercentChange(double percentChange){ this.dPercentChange = percentChange; }

}
