package my.weather.com.weatherapp.utilities;

import android.content.Context;

import java.util.HashMap;

import my.weather.com.weatherapp.R;
import my.weather.com.weatherapp.models.WeatherIcon;

public class Utils {

    public static HashMap<String, String> cities = new HashMap<String, String>() {{
        put("Tehran", "تهران");
        put("Karaj", "کرج");
        put("Amol", "آمل");
        put("Ardabil", "اردبیل");
        put("Mashhad", "مشهد");
        put("Isfahan", "اصفهان");
        put("Ahvaz", "اهواز");
        put("Qom", "قم");
        put("Tabriz", "تبریز");
        put("Urmia", "ارومیه");
        put("Zanjan", "زنجان");
        put("Rasht", "رشت");
        put("Kermanshah", "کرمانشاه");
        put("Varamin", "ورامین");
        put("Semnan", "سمنان");
        put("Bushehr", "بوشهر");
    }};

    public static int getWeatherIcon(Context context, HashMap<String, WeatherIcon> iconsList, int weatherId) {
        WeatherIcon icon = iconsList.get(weatherId + "");

        String prefix = "wi_";
        String iconName = "";

        if (icon != null) {
            iconName = icon.icon;
        } else {
            // default icon
            return R.string.wi_day_sunny;
        }

        if (!(weatherId > 699 && weatherId < 800) && !(weatherId > 899 && weatherId < 1000)) {
            if(!iconName.contains("day")) {
                iconName = "day_" + iconName;
            }
        }

        // Finally tack on the prefix.
        iconName = (prefix + iconName).replace("-", "_");

        return context.getResources().getIdentifier(iconName, "string", context.getPackageName());
    }

    public static String getTranslatedCityName(String cityName) {
        String r = Utils.cities.get(cityName);
        if(r == null)
            return cityName;
        else
            return r;
    }

}