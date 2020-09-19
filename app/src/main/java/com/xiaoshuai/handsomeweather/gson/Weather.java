package com.xiaoshuai.handsomeweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 城市天气信息
 */
public class Weather {

    /*城市的基本信息*/
    public Basic basic;
    /*请求状态*/
    public String status;
    /*当前天气情况*/
    public Now now;
    /*未来几天天气情况*/
    @SerializedName("daily_forecast")
    public List<Forecast> forecasts;
    /*空气质量*/
    public AQI aqi;
    /*生活指数*/
    public Suggestion suggestion;
    /*备注消息*/
    public String msg;

}
