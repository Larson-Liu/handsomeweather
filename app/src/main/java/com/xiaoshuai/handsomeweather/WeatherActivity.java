package com.xiaoshuai.handsomeweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xiaoshuai.handsomeweather.gson.Forecast;
import com.xiaoshuai.handsomeweather.gson.Weather;
import com.xiaoshuai.handsomeweather.util.JSONHandler;
import com.xiaoshuai.handsomeweather.util.OkHttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ImageView backgroundImage;
    private ScrollView weatherView;
    private TextView cityName;
    private TextView tmp;
    private TextView condTxt;
    private TextView aqiQlty;
    private TextView aqi;
    private TextView aqiPm25;
    private LinearLayout forecastLayout;
    private TextView latText;
    private TextView lonText;
    private TextView wind_sc;
    private TextView wind_dir;
    private TextView hum;
    private TextView fl;
    private TextView pres;
    private TextView comf_brf;
    private TextView comf_text;
    private TextView sport_brf;
    private TextView sport_text;
    private TextView cw_brf;
    private TextView cw_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*安卓5.0及其以上的系统支持的功能(将界面背景图与系统状态栏融合)*/
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //将活动布局显示在系统状态栏的上面
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //设置系统状态栏为透明状态
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        backgroundImage = (ImageView)findViewById(R.id.weather_background);
        weatherView = (ScrollView)findViewById(R.id.weather_data_view);
        cityName = (TextView)findViewById(R.id.city_name);
        tmp = (TextView)findViewById(R.id.tmp_text);
        condTxt = (TextView)findViewById(R.id.cond_txt);
        aqiQlty = (TextView)findViewById(R.id.aqi_qlty_text);
        aqi = (TextView)findViewById(R.id.aqi_text);
        aqiPm25 = (TextView)findViewById(R.id.aqi_pm25_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        latText = (TextView)findViewById(R.id.lat_text);
        lonText = (TextView)findViewById(R.id.lon_text);
        wind_sc = (TextView)findViewById(R.id.wind_sc);
        wind_dir = (TextView)findViewById(R.id.wind_dir);
        hum = (TextView)findViewById(R.id.hum_text);
        fl = (TextView)findViewById(R.id.fl_text);
        pres = (TextView)findViewById(R.id.pres_text);
        comf_brf = (TextView)findViewById(R.id.comf_brf_text);
        comf_text = (TextView)findViewById(R.id.comf_text);
        sport_brf = (TextView)findViewById(R.id.sport_brf_text);
        sport_text = (TextView)findViewById(R.id.sport_text);
        cw_brf = (TextView)findViewById(R.id.cw_brf_text);
        cw_text = (TextView)findViewById(R.id.cw_text);
        /*可以根据intent是否传入天气id来间接判断缓存中是否存在最近城市的天气数据*/
        Intent intent = getIntent();
        String weatherId = intent.getStringExtra("weather_id");
        if (!TextUtils.isEmpty(weatherId)) {
            /*从服务器中获取被选中的城市天气数据，并将天气数据存放到缓存中*/
            //隐藏空数据的天气界面
            weatherView.setVisibility(View.GONE);
            requestWeatherData(weatherId);
            //从服务器中加载图片
            requestImage();
        } else {
            /*从缓存中直接获取城市天气数据，并显示到天气界面中*/
            String weatherData = getIntent().getStringExtra("weather_data");
            Weather weather = new Gson().fromJson(weatherData,Weather.class);
            showWeatherData(weather);
            /*通过缓存中是否存在背景图Url来判断是否从服务器中获取并加载背景图*/
            SharedPreferences preferences = getSharedPreferences("weather_data",MODE_PRIVATE);
            String imageUrl = preferences.getString("image_url",null);
            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(this).load(imageUrl).into(backgroundImage);
            } else {
                requestImage();
            }
        }
    }

    /**
     * 根据天气id从服务器请求天气数据
     * @param weatherId
     */
    private void requestWeatherData(final String weatherId) {
        String address = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=060736f36c3e4dc2a55abe64b57619cd";
        OkHttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Toast.makeText(WeatherActivity.this,"网络异常，加载失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseResult = response.body().string();
                final Weather weather = JSONHandler.handleWeatherData(responseResult);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather!=null && "ok".equals(weather.status)) {
                            /*将天气数据存储到缓存中*/
                            SharedPreferences.Editor editor = getSharedPreferences("weather_data",MODE_PRIVATE).edit();
                            String weatherData = new Gson().toJson(weather);
                            editor.putString("weather_data",weatherData);
                            editor.apply();
                            showWeatherData(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * 将缓存中获取的天气数据显示到界面
     * @param weather
     */
    private void showWeatherData(Weather weather){
        //显示当前城市名
        cityName.setText(weather.basic.location);
        //显示天气
        tmp.setText(weather.now.currentTemperature);
        condTxt.setText(weather.now.condTxt);
        //显示天气质量
        aqiQlty.setText(weather.aqi.city.airQuality);
        aqi.setText(weather.aqi.city.aqi);
        aqiPm25.setText(weather.aqi.city.pm25);

        List<Forecast> forecasts = weather.forecasts;
        //添加之前先移除旧的天气预测数据
        forecastLayout.removeAllViews();
        //动态添加天气预测数据
        for (Forecast f:
             forecasts) {
            View forecastItem = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView date = (TextView)forecastItem.findViewById(R.id.date_text);
            TextView condInfo = (TextView)forecastItem.findViewById(R.id.cond_info_text);
            TextView maxTmp = (TextView)forecastItem.findViewById(R.id.max_tmp);
            TextView minTmp = (TextView)forecastItem.findViewById(R.id.min_tmp);
            date.setText(f.date);
            condInfo.setText(f.cond.condTxt);
            maxTmp.setText(f.tmp.max+"°");
            minTmp.setText(f.tmp.min+"°");
            forecastLayout.addView(forecastItem);
        }
        //显示纬度、经度、风力、湿度、体感温度、气压
        latText.setText(weather.basic.latitude);
        lonText.setText(weather.basic.longitude);
        wind_sc.setText(weather.now.windGrade+"级");
        wind_dir.setText(weather.now.windDirection);
        hum.setText(weather.now.humidity+"%");
        fl.setText(weather.now.feelsLike+"°");
        pres.setText(weather.now.pressure+"Pa");
        //显示生活指数(舒适度、运动指数、洗车指数)
        comf_brf.setText(weather.suggestion.comf.brf);
        comf_text.setText(weather.suggestion.comf.txt);
        sport_brf.setText(weather.suggestion.sport.brf);
        sport_text.setText(weather.suggestion.sport.txt);
        cw_brf.setText(weather.suggestion.cw.brf);
        cw_text.setText(weather.suggestion.cw.txt);
        //显示天气界面
        weatherView.setVisibility(View.VISIBLE);
    }

    /**
     * 从服务器中获取图片，添加到缓存中，再加载出来
     */
    private void requestImage() {
        String address="http://guolin.tech/api/bing_pic";
        OkHttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //如果从服务器中加载图片失败，则默认加载drawable中的背景图
                        Glide.with(WeatherActivity.this).load(R.drawable.weather_background).into(backgroundImage);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String imageUrl = response.body().string();
                /*将获取到的图片Url存储到缓存中*/
                SharedPreferences.Editor editor = getSharedPreferences("weather_data",MODE_PRIVATE).edit();
                editor.putString("image_url",imageUrl);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //加载图片
                        Glide.with(WeatherActivity.this).load(imageUrl).into(backgroundImage);
                    }
                });
            }
        });
    }
}