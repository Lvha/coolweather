package com.example.lvhao.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.lvhao.coolweather.gsonOne.lvhao;
import com.example.lvhao.coolweather.util.HttpUtil;
import com.example.lvhao.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdataService extends Service {
    public AutoUpdataService() {


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return null;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //更新天气数据
        updataWeather();
        //更新图片
        updataBingPic();

        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;  //这是8小时的毫秒
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this,AutoUpdataService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 更新天气数据
     */

    private void updataWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if (weatherString != null){
            //有缓存数据
            lvhao lvhao1 =Utility.handleWeatherResponse(weatherString);
            String weatherName = lvhao1.getResult().getToday().getCity();

            String weatherUrl = "http://v.juhe.cn/weather/index?format=2&cityname=" + weatherName + "&key=7c345d488001e8ff203c6b6a2619fc00";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    lvhao lvhao2 = Utility.handleWeatherResponse(responseText);
                    if (lvhao2 != null && "200".equals(lvhao2.getResultcode())){

                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdataService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();

                    }
                }
            });
        }
    }

    /**
     * 图片数据
     */

    private void updataBingPic(){
        String reqysetBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(reqysetBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdataService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();

            }
        });






    }

}
