package com.hawesome.demo.haweweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/1/2.
 */

public class Province extends DataSupport {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    private int id;
    private int provinceId;
    private String provinceName;
}
