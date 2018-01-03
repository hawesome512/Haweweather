package com.hawesome.demo.haweweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hawesome.demo.haweweather.gson.Forecast;
import com.hawesome.demo.haweweather.gson.Weather;
import com.hawesome.demo.haweweather.util.HttpUtil;
import com.hawesome.demo.haweweather.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    static final String PREF_WEATHER = "pref_weather";
    static final String PREF_BING = "pref_bing";
    static final String INTENT_WEATHER = "intent_weather";
    static final String URL_BING = "http://guolin.tech/api/bing_pic";
    Button btnHome;
    ImageView imgBg;
    TextView textCounty, textUpdate, textTempreture, textInfo, textAQI, textPM25, textComfortable,
            textCarWash, textSport;
    LinearLayout layoutForcast;
    SwipeRefreshLayout swipeRefreshLayout;
    DrawerLayout drawerLayout;
    String strWeatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        btnHome = findViewById(R.id.btn_home);
        textCounty = findViewById(R.id.text_county);
        textUpdate = findViewById(R.id.text_update);
        textTempreture = findViewById(R.id.text_tempreture);
        textInfo = findViewById(R.id.text_info);
        textAQI = findViewById(R.id.text_aqi);
        textPM25 = findViewById(R.id.text_pm25);
        textComfortable = findViewById(R.id.text_comfortable);
        textCarWash = findViewById(R.id.text_carwash);
        textSport = findViewById(R.id.text_sport);
        layoutForcast = findViewById(R.id.layout_forcast);
        imgBg = findViewById(R.id.img_bg);
        drawerLayout = findViewById(R.id.drawer_layout);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String strWeather = preferences.getString(PREF_WEATHER, null);
        if (strWeather != null) {
            Weather weather = new Gson().fromJson(strWeather, Weather.class);
            strWeatherId = weather.basic.weatherId;
            updateUI(weather);
        } else {
            strWeatherId = getIntent().getStringExtra(INTENT_WEATHER);
            updateWeather(strWeatherId);
        }
        String strBg = preferences.getString(PREF_BING, null);
        if (strBg != null) {
            Glide.with(this).load(strBg).into(imgBg);
        } else {
            loadBingPic();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View
                    .SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateWeather(strWeatherId);
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        Intent intentService=new Intent(this,WeatherService.class);
        startService(intentService);
    }

    public static void startAction(Context context, String weatherId) {
        Intent intent = new Intent(context, WeatherActivity.class);
        intent.putExtra(INTENT_WEATHER, weatherId);
        context.startActivity(intent);
    }

    private void loadBingPic() {
        Random random = new Random();
        final String url = "http://218.97.3.107/api/picture/" + random.nextInt(11) + ".jpg";
        HttpUtil.sendOkhttpRequest(URL_BING, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String strResponse = response.body().string();
                if (strResponse != null) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences
                            (WeatherActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.putString(PREF_BING, strResponse);
                    editor.putString(PREF_BING, url);
                    editor.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(url).into(imgBg);
                        }
                    });
                }
            }
        });
    }

    protected void updateWeather(String weatherId) {
        strWeatherId = weatherId;
        String url = "http://guolin.tech/api/weather?cityid=" + weatherId +
                "&key=1dc0b3784724444b9f87a9027d118a21";
        HttpUtil.sendOkhttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(WeatherActivity.this, "更新天气失败", Toast
                                .LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = Utility.handleWeatherJsonResponse(response);
                final Weather weather = new Gson().fromJson(json, Weather.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && weather.status.equals("ok")) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString(PREF_WEATHER, json);
                            editor.apply();
                            updateUI(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "更新天气失败", Toast
                                    .LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }

    private void updateUI(Weather weather) {
        textCounty.setText(weather.basic.cityName);
        textUpdate.setText(weather.basic.update.updateTime.split(" ")[1]);
        textTempreture.setText(weather.now.tempreture + "℃");
        textInfo.setText(weather.now.more.info);
        textAQI.setText(weather.aqi.city.aqi);
        textPM25.setText(weather.aqi.city.pm25);
        textComfortable.setText("舒适度：" + weather.suggestion.comfortable.info);
        textCarWash.setText("洗车指数:" + weather.suggestion.carWash.info);
        textSport.setText("运动建议：" + weather.suggestion.sport.info);
        layoutForcast.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_forcast, layoutForcast,
                    false);
            TextView textDate = view.findViewById(R.id.text_date);
            TextView textForcastInfo = view.findViewById(R.id.text_forcast_info);
            TextView textMaxTempreture = view.findViewById(R.id.text_max_tempreture);
            TextView textMinTempreture = view.findViewById(R.id.text_min_tempreture);
            textDate.setText(forecast.date);
            textForcastInfo.setText(forecast.more.info);
            textMaxTempreture.setText(forecast.tempreture.maxTempreture);
            textMinTempreture.setText(forecast.tempreture.minTempreture);
            layoutForcast.addView(view);
        }
    }
}
