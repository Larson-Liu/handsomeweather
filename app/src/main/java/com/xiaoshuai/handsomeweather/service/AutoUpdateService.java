package com.xiaoshuai.handsomeweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.xiaoshuai.handsomeweather.gson.Weather;
import com.xiaoshuai.handsomeweather.util.JSONHandler;
import com.xiaoshuai.handsomeweather.util.OkHttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    private static final String TAG = "AutoUpdateService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //重新请求当前城市的天气信息
        requestWeatherInfo();
        //重新请求天气背景图片
        requestImage();
        /*设置一个定时任务*/
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //延迟时间
        int delayedTime = 6*60*60*1000;
        //定时任务的触发时间
        long triggerAtMillis = System.currentTimeMillis()+delayedTime;
        //设置启动服务intent
        Intent intent1 = new Intent(this, AutoUpdateService.class);
        //设置启动服务pendingIntent，使得启动AutoUpdateWeatherService后会去执行onStartCommand()
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intent1,0);
        alarmManager.cancel(pendingIntent);
        //运行定时任务
        alarmManager.set(AlarmManager.RTC_WAKEUP,triggerAtMillis,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 从服务器中请求天气数据
     */
    private void requestWeatherInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("weather_data",MODE_PRIVATE);
        String weatherInfo = sharedPreferences.getString("weather_data",null);
        if (!TextUtils.isEmpty(weatherInfo)) {
            Weather weather = new Gson().fromJson(weatherInfo,Weather.class);
            if (weather!=null && "ok".equals(weather.status)) {
                String address = "http://guolin.tech/api/weather?cityid="+weather.basic.weatherId+"&key=060736f36c3e4dc2a55abe64b57619cd";
                OkHttpUtil.sendOkHttpRequest(address, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        /*更新缓存中的天气数据*/
                        String weatherData = response.body().string();
                        Weather weather1 = JSONHandler.handleWeatherData(weatherData);
                        String weatherInfo = new Gson().toJson(weather1);
                        SharedPreferences.Editor editor = getSharedPreferences("weather_data",MODE_PRIVATE).edit();
                        editor.putString("weather_data",weatherInfo);
                        editor.apply();
                    }
                });
            } else {
                Log.d(TAG, "requestWeatherInfo: 天气数据解析失败。");
            }
        } else {
            Log.d(TAG, "requestWeatherInfo: 从缓存中获取的天气数据weatherInfo为null.");
        }
    }

    /**
     * 从服务器中获取新的图片url
     */
    private void requestImage() {
        String address = "http://guolin.tech/api/bing_pic";
        OkHttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                /*更新缓存中的图片链接*/
                String imageUrl = response.body().string();
                SharedPreferences.Editor editor = getSharedPreferences("weather_data",MODE_PRIVATE).edit();
                editor.putString("image_url",imageUrl);
                editor.apply();
            }
        });
    }
}