package com.example.lvhao.coolweather.gsonOne;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Weather {

    public String resultcode;

    public String reason;

    public Result result;

    @SerializedName("future")
    public List<Future> futureList;

}








