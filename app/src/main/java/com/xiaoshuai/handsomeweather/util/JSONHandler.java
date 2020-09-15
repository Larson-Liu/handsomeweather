package com.xiaoshuai.handsomeweather.util;

import android.text.TextUtils;

import com.xiaoshuai.handsomeweather.db.City;
import com.xiaoshuai.handsomeweather.db.County;
import com.xiaoshuai.handsomeweather.db.Province;

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
}
