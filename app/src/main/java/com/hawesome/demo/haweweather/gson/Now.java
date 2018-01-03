package com.hawesome.demo.haweweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/1/2.
 */

public class Now {
    @SerializedName("tmp")
    public String tempreture;
    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
