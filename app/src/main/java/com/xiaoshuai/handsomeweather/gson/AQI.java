package com.xiaoshuai.handsomeweather.gson;

import com.google.gson.annotations.SerializedName;

public class AQI {

    /*城市空气质量指数*/
    public CityAQI city;
    public class CityAQI {
        /*空气质量指数*/
        public String aqi;
        /*PM2.5 1小时平均值(ug/m³)*/
        public String pm25;
        /*空气质量类别*/
        @SerializedName("qlty")
        public String airQuality;
    }

}
