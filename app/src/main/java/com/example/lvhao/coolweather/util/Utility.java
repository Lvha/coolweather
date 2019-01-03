package com.example.lvhao.coolweather.util;

import android.text.TextUtils;

import com.example.lvhao.coolweather.db.City;
import com.example.lvhao.coolweather.db.County;
import com.example.lvhao.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {


    //省 数据存到LitePal数据库中
    public static boolean handleprovinceResponse(String response){

        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0 ;i < allProvinces.length() ; i++ ){
                    JSONObject provincesObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provincesObject.getString("name"));
                    province.setProvinceCode(provincesObject.getInt("id"));
                    province.save();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return true;

    }

    //市
    public static boolean handleCityResponse(String response , int provinceId){

        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length() ; i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return true;

    }

    public static boolean handleCountyResponse(String response ,int cityId){

        if (!TextUtils.isEmpty(response)){

            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0 ;i < allCounties.length() ; i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        return true;

    }



}
