package my.weather.com.weatherapp.api;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import my.weather.com.weatherapp.api.utils.AppVolleyClient;
import my.weather.com.weatherapp.api.utils.VsRequest;
import my.weather.com.weatherapp.models.Weather;

public class API {

    private final static String BaseUrl = "http://api.openweathermap.org/data/2.5";
    private final static String API_KEY = "dd8bd98feba07af689ae305cd6b06ed9";

    public interface GET_WEATHER_LISTENER {
        void lambda(Weather response, String errorMessage);
    }

    public interface GET_5_DAY_FORECAST_LISTENER {
        void lambda(List<Weather> response, String errorMessage);
    }

    public static class GET {

        public static void currentWeather(Context context, double lat, double lng, String cityName, GET_WEATHER_LISTENER listener) {
            String url = BaseUrl + "/weather?APPID=" + API_KEY + "&units=metric&lang=fa";

            if(cityName != null) {
                try {
                    url += ("&q=" + URLEncoder.encode(cityName, "UTF-8"));
                } catch (UnsupportedEncodingException ignored) {}
            } else if(lat != 0 && lng != 0) {
                url += ("&lat=" + lat + "&lon=" + lng);
            }

            VsRequest request = new VsRequest(url, response -> {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject weatherObj = obj.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = obj.getJSONObject("main");

                    Weather weather = new Weather();
                    weather.cityName = obj.getString("name");
                    weather.temp = main.getLong("temp");
                    weather.minTemp = main.getLong("temp_min");
                    weather.maxTemp = main.getLong("temp_max");
                    weather.weather = weatherObj.getString("description");
                    weather.weatherId = weatherObj.getInt("id");

                    listener.lambda(weather, null);
                } catch (JSONException ignored) {
                    listener.lambda(null, "امکان دریافت اطلاعات وجود ندارد.");
                }
            }, error -> {
                listener.lambda(null, "امکان دریافت اطلاعات وجود ندارد.");
            });

            AppVolleyClient.getInstance(context).addToRequestQueue(request);
        }

        public static void fiveDayForecast(Context context, double lat, double lng, String cityName, GET_5_DAY_FORECAST_LISTENER listener) {
            String url = BaseUrl + "/forecast?APPID=" + API_KEY + "&units=metric&lang=fa";

            if(cityName != null) {
                try {
                    url += ("&q=" + URLEncoder.encode(cityName, "UTF-8"));
                } catch (UnsupportedEncodingException ignored) {}
            } else if(lat != 0 && lng != 0) {
                url += ("&lat=" + lat + "&lon=" + lng);
            }

            VsRequest request = new VsRequest(url, response -> {
                try {
                    ArrayList<Weather> result = new ArrayList<>();

                    JSONObject obj = new JSONObject(response);
                    JSONObject city = obj.getJSONObject("city");

                    JSONArray list = obj.getJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject listItem = list.getJSONObject(i);
                        JSONObject main = listItem.getJSONObject("main");
                        JSONObject weatherObj = listItem.getJSONArray("weather").getJSONObject(0);

                        Weather weather = new Weather();
                        weather.cityName = city.getString("name");
                        weather.temp = main.getLong("temp");
                        weather.minTemp = main.getLong("temp_min");
                        weather.maxTemp = main.getLong("temp_max");
                        weather.weather = weatherObj.getString("description");
                        weather.weatherId = weatherObj.getInt("id");
                        weather.timestamp = listItem.getLong("dt");

                        result.add(weather);
                    }

                    listener.lambda(result, null);
                } catch (JSONException ignored) {
                    Log.d("fd", ignored.getMessage());
                    listener.lambda(null, "امکان دریافت اطلاعات وجود ندارد.");
                }
            }, error -> {
                listener.lambda(null, "امکان دریافت اطلاعات وجود ندارد.");
            });

            AppVolleyClient.getInstance(context).addToRequestQueue(request);
        }

    }

}
