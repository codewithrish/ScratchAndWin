package com.gamingworld.scratchandwin.util;

public class TransactionDetail {

    private String tranStatus;
    private int tranCoins;
    private String tranMedium, tranDetails, date;


    public TransactionDetail() {
    }

    public TransactionDetail(String tranStatus, int tranCoins, String tranMedium, String tranDetails) {
        this.tranStatus = tranStatus;
        this.tranCoins = tranCoins;
        this.tranMedium = tranMedium;
        this.tranDetails = tranDetails;
    }

    public TransactionDetail(String tranStatus, int tranCoins, String tranMedium, String tranDetails, String date) {
        this.tranStatus = tranStatus;
        this.tranCoins = tranCoins;
        this.tranMedium = tranMedium;
        this.tranDetails = tranDetails;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTranStatus() {
        return tranStatus;
    }

    public void setTranStatus(String tranStatus) {
        this.tranStatus = tranStatus;
    }

    public int getTranCoins() {
        return tranCoins;
    }

    public void setTranCoins(int tranCoins) {
        this.tranCoins = tranCoins;
    }

    public String getTranMedium() {
        return tranMedium;
    }

    public void setTranMedium(String tranMedium) {
        this.tranMedium = tranMedium;
    }

    public String getTranDetails() {
        return tranDetails;
    }

    public void setTranDetails(String tranDetails) {
        this.tranDetails = tranDetails;
    }
}
