package com.hawesome.demo.haweweather;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hawesome.demo.haweweather.db.City;
import com.hawesome.demo.haweweather.db.County;
import com.hawesome.demo.haweweather.db.Province;
import com.hawesome.demo.haweweather.util.HttpUtil;
import com.hawesome.demo.haweweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/1/2.
 */

public class AreaFragment extends android.support.v4.app.Fragment {
    static final int LEVEL_PROVINCE = 0;
    static final int LEVEL_CITY = 1;
    static final int LEVEL_COUNTY = 2;
    int currentLevel = LEVEL_PROVINCE;
    Button btnBack;
    TextView textTitle;
    ListView listArea;

    List<Province> provinceList;
    List<City> cityList;
    List<County> countyList;
    Province selectedProvince;
    City selectedCity;
    County selectedCounty;
    List<String> dataList;
    ArrayAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_area, container, false);
        btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    currentLevel = LEVEL_CITY;
                } else if (currentLevel == LEVEL_CITY) {
                    currentLevel = LEVEL_PROVINCE;
                } else {

                }
                queryAreaFromDB();
            }
        });
        textTitle = view.findViewById(R.id.tv_title);
        listArea = view.findViewById(R.id.list_area);
        dataList = new ArrayList<>();
        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, dataList);
        listArea.setAdapter(adapter);
        listArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    currentLevel = LEVEL_CITY;
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    currentLevel = LEVEL_COUNTY;
                } else {
                    selectedCounty = countyList.get(position);
                    String weatherId=selectedCounty.getWeatherId();
                    FragmentActivity activity = getActivity();
                    if (activity instanceof MainActivity) {
                        WeatherActivity.startAction(getContext(), weatherId);
                        getActivity().finish();
                    }else if(activity instanceof WeatherActivity){
                        WeatherActivity weatherActivity=(WeatherActivity)activity;
                        weatherActivity.updateWeather(weatherId);
                        weatherActivity.drawerLayout.closeDrawers();
                    }
                }
                queryAreaFromDB();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryAreaFromDB();
    }

    private void queryAreaFromDB() {
        String url = null;
        if (currentLevel == LEVEL_PROVINCE) {
            btnBack.setVisibility(View.GONE);
            textTitle.setText("中国");
            provinceList = DataSupport.findAll(Province.class);
            if (provinceList.size() != 0) {
                dataList.clear();
                for (Province province : provinceList) {
                    dataList.add(province.getProvinceName());
                }
                adapter.notifyDataSetChanged();
                return;
            } else {
                url = "http://guolin.tech/api/china";
            }
        } else if (currentLevel == LEVEL_CITY) {
            btnBack.setVisibility(View.VISIBLE);
            textTitle.setText(selectedProvince.getProvinceName());
            cityList = DataSupport.where("provinceId=?", String.valueOf(selectedProvince
                    .getProvinceId())).find(City.class);
            if (cityList.size() != 0) {
                dataList.clear();
                for (City city : cityList) {
                    dataList.add(city.getCityName());
                }
                adapter.notifyDataSetChanged();
                return;
            } else {
                url = "http://guolin.tech/api/china/" + selectedProvince.getProvinceId();
            }
        } else {
            textTitle.setText(selectedCity.getCityName());
            countyList = DataSupport.where("cityId=?", String.valueOf(selectedCity.getCityId()))
                    .find(County.class);
            if (countyList.size() != 0) {
                dataList.clear();
                for (County county : countyList) {
                    dataList.add(county.getCountyName());
                }
                adapter.notifyDataSetChanged();
                return;
            } else {
                url = "http://guolin.tech/api/china/" + selectedProvince.getProvinceId()
                        + "/" + selectedCity.getCityId();
            }
        }
        if (url != null) {
            queryAreaFromServer(url);
        }
    }

    private void queryAreaFromServer(String url) {
        HttpUtil.sendOkhttpRequest(url, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                boolean saved = false;
                if (currentLevel == LEVEL_PROVINCE) {
                    saved = Utility.handleProvinceResponse(result);
                } else if (currentLevel == LEVEL_CITY) {
                    saved = Utility.handleCityResponse(result, selectedProvince.getProvinceId());
                } else {
                    saved = Utility.handleCountyResponse(result, selectedCity.getCityId());
                }
                if (saved) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            queryAreaFromDB();
                        }
                    });
                }
            }
        });
    }
}
