package my.weather.com.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import my.weather.com.weatherapp.adapters.WeatherForecastAdapter;
import my.weather.com.weatherapp.api.API;
import my.weather.com.weatherapp.models.Weather;
import my.weather.com.weatherapp.models.WeatherIcon;
import my.weather.com.weatherapp.utilities.Utils;

public class MainActivity extends BaseActivity {

    Context mContext;

    private TextView mTemp, mHumidity, mTempHigh, mTempLow, mName, mWeather, mWeatherIcon;

    HashMap<String, WeatherIcon> weatherIconsList = new HashMap<>();
    private List<Weather> forecastsList = new ArrayList<>();

    double lat = 0;
    double lng = 0;
    String cityName = "کرج";
    boolean firstFetch = true;

    WeatherForecastAdapter mWeatherForecastRecyclerViewAdapter;
    RecyclerView forecastsRecyclerView;

    Button getMyCurrentLocationButton;
    EditText cityNameEditText;

    MaterialDialog dialog;

    ImageView backgroundWeatherImageView;

    ConstraintLayout loading;
    NestedScrollView scrollView;
    CardView cardForecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity_main);

        mContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        try {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_right_white));
            toolbar.setNavigationOnClickListener(v -> finish());
        } catch (Exception ignored) {}

        weatherIconsList = loadWeatherIconModels();
        setupUI();
        setForecastsRecyclerView();

        fetchWeather(lat, lng, cityName);
    }

    public void setupUI() {
        Typeface weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");

        forecastsRecyclerView = findViewById(R.id.forecastsRecyclerView);
        mTemp = findViewById(R.id.temp);
        mTempHigh = findViewById(R.id.maxTemp);
        mTempLow = findViewById(R.id.minTemp);
        mName = findViewById(R.id.name);
        mWeather = findViewById(R.id.weather);
        mWeatherIcon = findViewById(R.id.weatherIcon);

        mWeatherIcon.setTypeface(weatherFont);

        loading = findViewById(R.id.loading);
        scrollView = findViewById(R.id.scrollView);
        backgroundWeatherImageView = findViewById(R.id.backgroundWeatherImageView);
        cardForecast = findViewById(R.id.cardForecast);

        mName.setOnClickListener(v -> {
            dialog = setDialogForSourceChoose();
            cityName = null;
            dialog.show();
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    MaterialDialog setDialogForSourceChoose() {
        dialog = new MaterialDialog.Builder(mContext)
                .customView(R.layout.weather_dialog_choose_source, false)
                .show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.findViewById(R.id.submit_button).setOnClickListener(v -> {
            if(cityNameEditText.getText() != null && !cityNameEditText.getText().toString().equals("")) {
                cityName = cityNameEditText.getText().toString();
                lat = 0;
                lng = 0;
                fetchWeather(lat, lng, cityName);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.cancel_button).setOnClickListener(v -> {
            dialog.cancel();
        });

        cityNameEditText = (EditText) dialog.findViewById(R.id.cityNameEditText);

        return dialog;
    }

    void setForecastsRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mWeatherForecastRecyclerViewAdapter = new WeatherForecastAdapter(mContext, forecastsList, weatherIconsList, forecastsRecyclerView, true);
        forecastsRecyclerView.setLayoutManager(mLayoutManager);
        forecastsRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, mLayoutManager.getOrientation()));
        forecastsRecyclerView.setAdapter(mWeatherForecastRecyclerViewAdapter);
        mWeatherForecastRecyclerViewAdapter.setOnItemClickListener((view, position) -> mWeatherForecastRecyclerViewAdapter.toggleExpandableItem(position));
    }

    void setBackgroundWeatherImage(String weatherName) {
        int _sunny = R.drawable.weather_sun;
        int _snowing = R.drawable.weather_snowing;
        int _raining = R.drawable.weather_rain;
        int _cloudy = R.drawable.weather_cloud;
        int _half_cloudy = R.drawable.weather_half_cloud;
        int _haze = R.drawable.weather_haze;

        int imageName = 0;

        if(weatherName.contains("صاف")) {
            imageName = _sunny;
        } else if(weatherName.contains("برف")) {
            imageName = _snowing;
        } else if(weatherName.contains("باران")) {
            imageName = _raining;
        } else if(weatherName.contains("پوشیده") && weatherName.contains("ابر")) {
            imageName = _cloudy;
        } else if(weatherName.contains("ابر")) {
            imageName = _half_cloudy;
        } else if(weatherName.contains("مه") || weatherName.contains("گرد")) {
            imageName = _haze;
        }

        if(backgroundWeatherImageView != null && imageName != 0) {
            backgroundWeatherImageView.setImageResource(imageName);
        } else if(backgroundWeatherImageView != null) {
            backgroundWeatherImageView.setImageResource(0);
        }
    }

    void fetchWeather(double lat, double lng, String cityName) {
        if (!firstFetch) {
            Toast.makeText(mContext, "در حال دریافت آب و هوا", Toast.LENGTH_SHORT).show();
        }
        fetchCurrentWeather(lat, lng, cityName);
        fetchWeatherForecasts(lat, lng, cityName);
    }

    void fetchCurrentWeather(double lat, double lng, String cityName) {
        API.GET.currentWeather(mContext, lat, lng, cityName, (response, errorMessage) -> {
            loading.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

            if(response != null) {
                firstFetch = false;
                mName.setText(Utils.getTranslatedCityName(response.cityName));
                mWeather.setText(response.weather);
                mTemp.setText(response.temp + "" + (char) 0x00B0);
                mTempHigh.setText(response.maxTemp + "" + (char) 0x00B0);
                mTempLow.setText(response.minTemp + "" + (char) 0x00B0);
                try {
                    mWeatherIcon.setText(Utils.getWeatherIcon(mContext, weatherIconsList, response.weatherId));
                } catch (Exception ignored) {}

                setBackgroundWeatherImage(response.weather);
            } else {
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void fetchWeatherForecasts(double lat, double lng, String cityName) {
        API.GET.fiveDayForecast(mContext, lat, lng, cityName, (response, errorMessage) -> {
            if(response != null) {
                firstFetch = false;
                cardForecast.setVisibility(View.VISIBLE);

                ArrayList<Weather> p = new ArrayList<>();
                for(Weather w : response) {
                    GregorianCalendar gregorianCalendar = new GregorianCalendar();
                    gregorianCalendar.setTimeInMillis(w.timestamp * 1000L);

                    JalaliCalendar calendar = new JalaliCalendar();
                    calendar.fromGregorian(gregorianCalendar);

                    Date timestampDate = new Date(w.timestamp * 1000L);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);

                    w.dayOfWeek = calendar.getDayOfWeekString();
                    w.timeOfDay = simpleDateFormat.format(timestampDate);

                    if(p.size() > 0) {
                        int lastItemIndex = p.size() - 1;

                        if(p.get(lastItemIndex).dayOfWeek.equals(w.dayOfWeek)) {
                            p.get(lastItemIndex).weatherHours.add(w);
                        } else {
                            p.add(w);
                        }
                    } else {
                        p.add(w);
                    }
                }

                forecastsList.clear();
                forecastsList.addAll(p);
                mWeatherForecastRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private HashMap<String, WeatherIcon> loadWeatherIconModels() {
        return new Gson().fromJson(Helpers.loadJSONFromAssets(mContext, "weatherIconsIntegration.json"), new TypeToken<HashMap<String, WeatherIcon>>() {}.getType());
    }
}
