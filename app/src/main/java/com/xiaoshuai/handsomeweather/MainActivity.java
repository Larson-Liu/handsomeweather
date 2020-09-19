package com.xiaoshuai.handsomeweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.xiaoshuai.handsomeweather.fragment.ChooseAreaFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*根据缓存中是否存在最近的城市天气数据来加载界面*/
        SharedPreferences sharedPreferences=getSharedPreferences("weather_data",MODE_PRIVATE);
        String weatherData=sharedPreferences.getString("weather_data",null);
        if (!TextUtils.isEmpty(weatherData)) {
            /*直接进入天气界面，同时将缓存中的天气数据传入过去*/
            Intent intent=new Intent(this,WeatherActivity.class);
            intent.putExtra("weather_data",weatherData);
            startActivity(intent);
            finish();
        } else {
            /*进入选择城市界面*/
            setContentView(R.layout.activity_main);
            //动态加载碎片布局，静态加载不出碎片布局
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.choose_area_fragment,new ChooseAreaFragment());
            transaction.commit();
        }
    }
}