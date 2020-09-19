package com.xiaoshuai.handsomeweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {

    /*云层指数*/
    public String cloud;
    /*天气代码*/
    @SerializedName("cond_code")
    public String condCode;
    /*天气描述*/
    @SerializedName("cond_txt")
    public String condTxt;
    /*体感温度(℃)*/
    @SerializedName("fl")
    public String feelsLike;
    /*湿度(%)*/
    @SerializedName("hum")
    public String humidity;
    /*降雨量(mm)*/
    @SerializedName("pcpn")
    public String precipitation;
    /*气压(Pa)*/
    @SerializedName("pres")
    public String pressure;
    /*当前温度(℃)*/
    @SerializedName("tmp")
    public String currentTemperature;
    /*能见度(km)*/
    @SerializedName("vis")
    public String visibility;
    /*风向(角度)*/
    @SerializedName("wind_deg")
    public String windDegree;
    /*风向(方向)*/
    @SerializedName("wind_dir")
    public String windDirection;
    /*风力等级*/
    @SerializedName("wind_sc")
    public String windGrade;
    /*风速(Kmp/h)*/
    @SerializedName("wind_spd")
    public String windSpeed;

}
