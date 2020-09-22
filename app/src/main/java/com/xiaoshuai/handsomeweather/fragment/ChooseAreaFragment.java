package com.xiaoshuai.handsomeweather.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.xiaoshuai.handsomeweather.MainActivity;
import com.xiaoshuai.handsomeweather.R;
import com.xiaoshuai.handsomeweather.WeatherActivity;
import com.xiaoshuai.handsomeweather.db.City;
import com.xiaoshuai.handsomeweather.db.County;
import com.xiaoshuai.handsomeweather.db.Province;
import com.xiaoshuai.handsomeweather.gson.Weather;
import com.xiaoshuai.handsomeweather.util.JSONHandler;
import com.xiaoshuai.handsomeweather.util.OkHttpUtil;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 给选择地区的碎片设置功能
 */
public class ChooseAreaFragment extends Fragment {

    /*定义当前显示数据类型*/
    public static final int LEVEL_PROVINCE=1;
    public static final int LEVEL_CITY=2;
    public static final int LEVEL_COUNTY=3;
    private int currentLevel;

    /*定义被选中的省、市以及从数据库中查询的数据集*/
    private Province selectedProvince;
    private List<Province> provinces;
    private City selectedCity;
    private List<City> cities;
    private List<County> counties;

    /*定义碎片布局中的控件对象*/
    private Button backButton;
    private TextView title;
    private ProgressBar progressBar;
    private ListView areaList;

    /*定义适配器与显示的数据集*/
    private ArrayAdapter<String> areaAdapter;
    //注意这里的dataList是要调用add()，所以要获取实例，否则会出现空指针异常
    private List<String> dataList=new ArrayList<>();

    /*请求服务器URL常量*/
    private final String BASEADDRESS="http://guolin.tech/api/china";

    /**
     * 加载碎片布局，给ListView控件设置适配器
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return 设置好适配器的碎片布局
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View chooseAreaView=inflater.inflate(R.layout.choose_area,container,false);
        backButton=(Button) chooseAreaView.findViewById(R.id.back_button);
        title=(TextView) chooseAreaView.findViewById(R.id.title_text);
        progressBar=(ProgressBar) chooseAreaView.findViewById(R.id.progress_circular_bar);
        areaList=(ListView) chooseAreaView.findViewById(R.id.area_list);
        areaAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        areaList.setAdapter(areaAdapter);
        return chooseAreaView;
    }

    /**
     * 给碎片布局加载数据
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvince();
        areaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               if (LEVEL_PROVINCE==currentLevel) {
                   selectedProvince=provinces.get(i);
                   queryCity();
               } else if (LEVEL_CITY==currentLevel) {
                   selectedCity=cities.get(i);
                   queryCounty();
               } else if (LEVEL_COUNTY==currentLevel) {
                   /*点击县进入对应城市天气界面，并传入天气id*/
                   String weatherId=counties.get(i).getWeatherId();
                   if (getActivity() instanceof MainActivity) {
                       /*如果选择地区的碎片在MainActivity中，即为程序最初启动后显示的城市选择菜单*/
                       Intent intent=new Intent(getContext(), WeatherActivity.class);
                       intent.putExtra("weather_id",weatherId);
                       startActivity(intent);
                       getActivity().finish();
                   } else if (getActivity() instanceof WeatherActivity) {
                       /*如果选择地区的碎片在WeatherActivity中，即为右滑显示城市选择菜单*/
                       WeatherActivity weatherActivity = (WeatherActivity)getActivity();
                       //调用public的drawerLayout关闭城市选择菜单
                       weatherActivity.drawerLayout.closeDrawer(GravityCompat.START);
                       //选择完成后重新回到省份选择界面
                       currentLevel = LEVEL_PROVINCE;
                       //查询省份数据并显示
                       queryProvince();
                       //调用public的swipeRefresh启动下拉刷新
                       weatherActivity.swipeRefresh.setRefreshing(true);
                       //调用public的请求数据方法从服务器中获取新选择城市的天气信息
                       weatherActivity.requestWeatherData(weatherId);
                   }
               }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LEVEL_COUNTY==currentLevel) {
                    queryCity();
                } else if (LEVEL_CITY==currentLevel) {
                    queryProvince();
                }
            }
        });
    }

    /**
     * 先从省数据库表中获取数据，如果没有再从服务器中获取省级数据并添加到省数据库表中，再到省数据库表中获取数据
     */
    private void queryProvince() {
        title.setText("省  份");
        backButton.setVisibility(View.GONE);
        provinces=LitePal.findAll(Province.class);
        if (provinces!=null && provinces.size()>0) {
            dataList.clear();
            for (Province p:
                 provinces) {
                dataList.add(p.getProvinceName());
            }
            areaAdapter.notifyDataSetChanged();
            areaList.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        } else {
            queryFromServer(BASEADDRESS,"province");
        }
    }

    /**
     * 先从市数据库表中获取数据，如果没有再从服务器中获取市级数据并添加到市数据库表中，再到市数据库表中获取数据
     */
    private void queryCity() {
        title.setText("市");
        backButton.setVisibility(View.VISIBLE);
        cities=LitePal.where("provinceId=?",String.valueOf(selectedProvince.getProvinceCode())).find(City.class);
        if (cities!=null && cities.size()>0) {
            dataList.clear();
            for (City c:
                 cities) {
                dataList.add(c.getCityName());
            }
            areaAdapter.notifyDataSetChanged();
            areaList.setSelection(0);
            currentLevel=LEVEL_CITY;
        } else {
            String address=BASEADDRESS+"/"+selectedProvince.getProvinceCode();
            queryFromServer(address,"city");
        }
    }

    /**
     * 先从县数据库表中获取数据，如果没有再从服务器中获取县级数据并添加到县数据库表中，再到县数据库表中获取数据
     */
    private void queryCounty() {
        title.setText("县  区");
        backButton.setVisibility(View.VISIBLE);
        counties=LitePal.where("cityId=?",String.valueOf(selectedCity.getCityCode())).find(County.class);
        if (counties!=null && counties.size()>0) {
            dataList.clear();
            for (County c:
                 counties) {
                dataList.add(c.getCountyName());
            }
            areaAdapter.notifyDataSetChanged();
            areaList.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        } else {
            String address=BASEADDRESS+"/"+selectedProvince.getProvinceCode()+"/"+selectedCity.getCityCode();
            queryFromServer(address,"county");
        }
    }
    /**
     * 从服务器获取数据,type用于区分从服务器返回的省、市、县数据
     * @param address
     * @param type
     */
    private void queryFromServer(String address,final String type) {
        showProgressBar();
        OkHttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                //回到主线程执行UI更新操作
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressBar();
                        //出现错误时页面数据不变
                        if (LEVEL_PROVINCE==currentLevel) {
                            title.setText("省  份");
                            backButton.setVisibility(View.GONE);
                        } else if (LEVEL_CITY==currentLevel) {
                            title.setText("市");
                        }
                        Toast.makeText(getContext(),"网络出错，加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                String result=response.body().string();
                boolean handleResult=false;
                switch (type) {
                    case "province":
                        handleResult=JSONHandler.handleProvinceData(result);
                        break;
                    case "city":
                        handleResult=JSONHandler.handleCityData(result,selectedProvince.getProvinceCode());
                        break;
                    case "county":
                        handleResult=JSONHandler.handleCountyData(result,selectedCity.getCityCode());
                        break;
                    default:
                        break;
                }
                if (handleResult) {
                    //切换到主线程中执行UI更新操作
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressBar();
                            switch (type) {
                                case "province":
                                    queryProvince();
                                    break;
                                case "city":
                                    queryCity();
                                    break;
                                case "county":
                                    queryCounty();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示滚动条
     */
    private void showProgressBar() {
        if (progressBar.getVisibility()==View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
        }
        //禁止用户与界面进行交互
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     * 关闭滚动条
     */
    private void closeProgressBar() {
        if (progressBar.getVisibility()==View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
        //恢复用户与界面的交互
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
