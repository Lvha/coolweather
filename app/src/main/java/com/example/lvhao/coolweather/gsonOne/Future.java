package com.example.lvhao.coolweather.gsonOne;

import com.google.gson.annotations.SerializedName;

public class Future {

    @SerializedName("temperature")
    public String futureTemperature;

    @SerializedName("weather")
    public String futureWeather;


    //风向
    @SerializedName("wind")
    public String futureWind;


    @SerializedName("week")
    public String futureWeek;



}
