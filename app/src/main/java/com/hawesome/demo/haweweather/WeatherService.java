package com.hawesome.demo.haweweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hawesome.demo.haweweather.gson.Weather;
import com.hawesome.demo.haweweather.util.HttpUtil;
import com.hawesome.demo.haweweather.util.Utility;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherService extends Service {
    public WeatherService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updatePic();
        updateWeather();
        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intentService=new Intent(this,WeatherService.class);
        PendingIntent pi=PendingIntent.getService(this,0,intentService,0);
        long interval= SystemClock.elapsedRealtime()+ 60*1000;
        alarmManager.cancel(pi);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,interval,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void updateWeather(){
        final SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String prefWeather= preferences.getString(WeatherActivity.PREF_WEATHER,null);
        if(!TextUtils.isEmpty(prefWeather)){
            Weather weather=new Gson().fromJson(prefWeather,Weather.class);
            String url = "http://guolin.tech/api/weather?cityid=" + weather.basic.weatherId +
                    "&key=1dc0b3784724444b9f87a9027d118a21";
            HttpUtil.sendOkhttpRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json= Utility.handleWeatherJsonResponse(response);
                    preferences.edit().putString(WeatherActivity.PREF_WEATHER,json).apply();
                }
            });
        }
    }

    private void updatePic(){
        Random random = new Random();
        final String url = "http://218.97.3.107/api/picture/" + random.nextInt(11) + ".jpg";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences
                (this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(WeatherActivity.PREF_BING, url);
        editor.apply();
    }
}
