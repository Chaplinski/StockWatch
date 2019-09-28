package com.example.stockwatch;

public class Stock {

    private String sSymbol;
    private String sCompany;
    private double dCurrentPrice;
    private double dChangeToday;

    public String getSymbol(){ return sSymbol; }

    public void setSymbol(String newSymbol){ this.sSymbol = newSymbol; }

    public String getCompany(){ return sCompany; }

    public void setCompany(String newCompany){ this.sCompany = newCompany; }

    public double getCurrentPrice(){ return dCurrentPrice; }

    public void setCurrentPrice(double newPrice){ this.dCurrentPrice = newPrice; }

    public double getChangeToday(){ return dChangeToday; }

    public void setChangeToday(double newChange){ this.dChangeToday = newChange; }

}
