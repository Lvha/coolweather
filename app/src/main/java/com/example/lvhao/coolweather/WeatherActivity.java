package com.example.lvhao.coolweather;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.lvhao.coolweather.gsonOne.lvhao;
import com.example.lvhao.coolweather.util.HttpUtil;
import com.example.lvhao.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weaherLayout;

    private TextView titleCity;

    private TextView titleUpdataTime;

    private TextView degreeText;

    private  TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView apiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private Button addButton;

    private ImageView bingPicImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //只支持5.0以上版本
        if (Build.VERSION.SDK_INT >= 21){
            //拿到当前活动的DecorView
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);



        }

        weaherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdataTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);

        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);

        addButton = (Button)findViewById(R.id.add_button);

        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);






        //?????
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this,addCity.class);
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("weather",null);
                editor.apply();
                startActivity(intent);
                finish();
            }


        });
        String weatherString = prefs.getString("weather",null);

        if (weatherString != null){

            lvhao weather = Utility.handleWeatherResponse(weatherString);


            assert weather != null;
            showWeatherInfo(weather);


        } else
            {
            //利用Intent传值
            String weatherId = getIntent().getStringExtra("weather_id");
            weaherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        String bingPic = prefs.getString("bing_pic",null);
        if (bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();

        }


    }

    public void requestWeather(final String weatherId){

        String weatherUrl = "http://v.juhe.cn/weather/index?format=2&cityname=" + weatherId + "&key=7c345d488001e8ff203c6b6a2619fc00";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responseText = response.body().string();
                final lvhao weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "200".equals(weather.getResultcode())){

                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);


                        }else {
                            Toast.makeText(WeatherActivity.this,"获取失败！！！4545454545！" ,Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取失败！！！！" ,Toast.LENGTH_SHORT).show();
                    }
                });



            }


        });
    }

    //根据城市名称请求天气信息

    private void showWeatherInfo(lvhao weather){
        String cityName = weather.getResult().getToday().getCity();
        String updateTime = weather.getResult().getSk().getTime();
        String degree = weather.getResult().getSk().getTemp();
        String weatherInfo = weather.getResult().getToday().getWeather();

        titleCity.setText(cityName);
        titleUpdataTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        //删除forecastLayout 下面的控件
         forecastLayout.removeAllViews();

        for (lvhao.ResultBean.FutureBean future :weather.getResult().getFuture()){

            View view = LayoutInflater.from(this).inflate(R.layout.forecast_itme,forecastLayout,false);

            TextView weekText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView weakText = (TextView)view.findViewById(R.id.max_text);
            TextView windText = (TextView)view.findViewById(R.id.min_text);
            weekText.setText(future.getWeek());
            infoText.setText(future.getTemperature());
            weakText.setText(future.getWeather());
            windText.setText(future.getWind());


            forecastLayout.addView(view);

        }


        String dressing_advice = "穿衣建议 : " + weather.getResult().getToday().getDressing_advice();

        comfortText.setText(dressing_advice);


        weaherLayout.setVisibility(View.VISIBLE);

    }

    //加载图片

    private void loadBingPic(){

        String reqysetBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(reqysetBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });

            }
        });


    }



}