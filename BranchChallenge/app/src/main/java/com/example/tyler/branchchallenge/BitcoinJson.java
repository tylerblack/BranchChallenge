package com.example.tyler.branchchallenge;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Tyler on 1/29/2016.
 */
public class BitcoinJson {

    @SerializedName("ask")
    public double ask;

    @SerializedName("bid")
    public double bid;

    @SerializedName("last")
    public double last;

    @SerializedName("timestamp")
    public String timestamp;

    @SerializedName("volume_btc")
    public double volumeBtc;

    @SerializedName("volume_percent")
    public double volumePercent;

    @SerializedName("24h_avg")
    public double dailyAvg;
}
