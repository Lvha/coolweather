package com.example.lvhao.coolweather.gsonOne;

import com.google.gson.annotations.SerializedName;

public class Result {

    public Sk sk;

    public Today today;

    public class Sk{

        @SerializedName("temp")
        public String skTemp;

        @SerializedName("time")
        public String skTime;



    }



    public class Today{

        @SerializedName("city")
        public String todayCity;

        @SerializedName("weather")
        public String todayWeather;

    }

    @SerializedName("dressing_advice")
    public String dressingAdvice;

    @SerializedName("uv_index")
    public String uvIndex;

    @SerializedName("wash_index")
    public String washIndex;





}
