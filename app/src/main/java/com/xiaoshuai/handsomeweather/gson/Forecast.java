package com.xiaoshuai.handsomeweather.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {

    /*当地日期*/
    public String date;
    /*天气状况*/
    public Cond cond;
    /*温度*/
    public Tmp tmp;

    public class Cond {
        /*天气描述*/
        @SerializedName("txt_d")
        public String condTxt;
    }
    public class Tmp {
        /*最高温度(℃)*/
        public String max;
        /*最低温度(℃)*/
        public String min;
    }

}
