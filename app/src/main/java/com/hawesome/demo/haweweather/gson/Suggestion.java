package com.hawesome.demo.haweweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/1/2.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfortable comfortable;
    @SerializedName("cw")
    public CarWash carWash;
    public Sport sport;

    public class Comfortable{
        @SerializedName("txt")
        public String info;
    }
    public class CarWash{
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
