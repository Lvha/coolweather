package com.example.lvhao.coolweather.db;

import org.litepal.crud.DataSupport;

public class County extends DataSupport {

    //县

    private int id;
    private String countyName;  //县名
    private String weatherId;   //县的代码
    private String cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
}