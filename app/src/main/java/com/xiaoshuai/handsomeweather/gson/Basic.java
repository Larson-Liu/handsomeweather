package com.xiaoshuai.handsomeweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 城市基本信息
 */
public class Basic {

    /*天气id*/
    @SerializedName("cid")
    public String weatherId;
    /*位置,地点*/
    public String location;
    /*对应的市*/
    @SerializedName("parent_city")
    public String parentCity;
    /*对应的省份*/
    @SerializedName("admin_area")
    public String province;
    /*国家*/
    @SerializedName("cnty")
    public String country;
    /*纬度*/
    @SerializedName("lat")
    public String latitude;
    /*经度*/
    @SerializedName("lon")
    public String longitude;
    /*时区*/
    @SerializedName("tz")
    public String timezone;
    /*数据更新时间*/
    public Update update;

    public class Update {
        /*更新时间*/
        @SerializedName("loc")
        public String updateTime;
        /*世界协调时间*/
        @SerializedName("utc")
        public String universalTimeCoordinated;
    }
}
