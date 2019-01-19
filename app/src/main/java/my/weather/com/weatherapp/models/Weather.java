package my.weather.com.weatherapp.models;

import java.util.ArrayList;

public class Weather {
    public String cityName;
    public long temp;
    public long maxTemp;
    public long minTemp;
    public String weather;
    public int weatherId;
    public long timestamp;
    public String timeOfDay;
    public String dayOfWeek;
    public ArrayList<Weather> weatherHours = new ArrayList<>();
}