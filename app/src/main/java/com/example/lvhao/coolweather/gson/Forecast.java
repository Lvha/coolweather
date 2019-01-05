package com.example.lvhao.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {

    //预报

    public String date;

    @SerializedName("cond")
    public More more;

    @SerializedName("tmp")
    public Temperature temperature;

    public class Temperature {
        public String max;
        public String min;
    }

    public class More {
        @SerializedName("text_d")
        public String info;
    }

}
