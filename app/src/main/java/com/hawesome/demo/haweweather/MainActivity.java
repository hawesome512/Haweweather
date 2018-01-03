package com.hawesome.demo.haweweather;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.hawesome.demo.haweweather.gson.Weather;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String strWeather=preferences.getString(WeatherActivity.PREF_WEATHER,null);
        if(strWeather!=null){
            Weather weather= new Gson().fromJson(strWeather,Weather.class);
            WeatherActivity.startAction(this,weather.basic.weatherId);
            finish();
        }
    }
}
