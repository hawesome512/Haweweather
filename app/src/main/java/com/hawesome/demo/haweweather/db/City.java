package com.hawesome.demo.haweweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/1/2.
 */

public class City extends DataSupport {
    private int id;
    private int cityId;
    private int provinceId;
    private String cityName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
