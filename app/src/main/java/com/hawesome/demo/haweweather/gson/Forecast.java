package com.hawesome.demo.haweweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/1/2.
 */

public class Forecast {
    public String date;
    @SerializedName("cond")
    public More more;
    @SerializedName("tmp")
    public Tempreture tempreture;

    public class More{
        @SerializedName("txt_d")
        public String info;
    }
    public class Tempreture{
        @SerializedName("max")
        public String maxTempreture;
        @SerializedName("min")
        public String minTempreture;
    }
}
