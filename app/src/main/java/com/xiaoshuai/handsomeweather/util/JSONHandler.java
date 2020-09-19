package com.xiaoshuai.handsomeweather.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.xiaoshuai.handsomeweather.db.City;
import com.xiaoshuai.handsomeweather.db.County;
import com.xiaoshuai.handsomeweather.db.Province;
import com.xiaoshuai.handsomeweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 处理服务器返回的省、市、县级JSON数据并添加至本地数据库工具类
 */
public class JSONHandler {
    /**
     * 用JSONObject解析服务器返回的省级JSON数据并添加至本地数据库
     * @param provinceResult
     * @return 解析并添加的结果
     */
    public static boolean handleProvinceData(String provinceResult) {
        if (!TextUtils.isEmpty(provinceResult)) {
            try {
                JSONArray provinceArray=new JSONArray(provinceResult);
                for (int i = 0; i < provinceArray.length(); i++) {
                    JSONObject provinceObject=provinceArray.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.setProvinceName(provinceObject.getString("name"));
                    province.save();
                }
                return true;
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 用JSONObject解析服务器返回的市级JSON数据并添加至本地数据库
     * @param cityResult
     * @param provinceId
     * @return 解析并添加的结果
     */
    public static boolean handleCityData(String cityResult,int provinceId) {
        if (!TextUtils.isEmpty(cityResult)) {
            try {
                JSONArray cityArray=new JSONArray(cityResult);
                for (int i = 0; i < cityArray.length(); i++) {
                    JSONObject cityObject=cityArray.getJSONObject(i);
                    City city=new City();
                    city.setCityCode(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 用JSONObject解析服务器返回的县级JSON数据并添加至本地数据库
     * @param countyResult
     * @param cityId
     * @return 解析并添加的结果
     */
    public static boolean handleCountyData(String countyResult,int cityId) {
        if (!TextUtils.isEmpty(countyResult)) {
            try {
                JSONArray countyArray=new JSONArray(countyResult);
                for (int i = 0; i < countyArray.length(); i++) {
                    JSONObject countyObject=countyArray.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 用Gson将服务器返回的天气数据解析成Weather对象
     * @param weatherData
     * @return Weather实例
     */
    public static Weather handleWeatherData(String weatherData) {
        if (!TextUtils.isEmpty(weatherData)) {
            try {
                //单个Json对象
                JSONObject jsonObject=new JSONObject(weatherData);
                //根据"HeWeather"得到JSONArray
                JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
                //第一个jsonObject即为城市天气数据对象，将此转化成json字符串
                String weather_data=jsonArray.get(0).toString();
                //将天气json字符串数据解析成Weather对象
                Weather weather=new Gson().fromJson(weather_data,Weather.class);
                return weather;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
