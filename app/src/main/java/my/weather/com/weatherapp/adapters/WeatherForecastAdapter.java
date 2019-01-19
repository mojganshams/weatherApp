package my.weather.com.weatherapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import my.weather.com.weatherapp.ImprovedDividerItemDecoration;
import my.weather.com.weatherapp.JalaliCalendar;
import my.weather.com.weatherapp.OnClickListener;
import my.weather.com.weatherapp.R;
import my.weather.com.weatherapp.models.Weather;
import my.weather.com.weatherapp.models.WeatherIcon;
import my.weather.com.weatherapp.utilities.Utils;


public class WeatherForecastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private Typeface weatherFont;
    private HashMap<String, WeatherIcon> iconsList = new HashMap<>();
    public List<Weather> itemList = new ArrayList<>();
    public RecyclerView recyclerView;
    private OnClickListener mOnClickListener;

    private boolean isParent = true;

    public class WeatherForecastCardViewHolder extends RecyclerView.ViewHolder {

        TextView dayOfWeek, timeOfDay, weatherIcon, weather, maxTemp, minTemp;
        RecyclerView childrenRecyclerView;

        WeatherForecastCardViewHolder(View view) {
            super(view);

            dayOfWeek = view.findViewById(R.id.dayOfWeek);
            timeOfDay = view.findViewById(R.id.timeOfDay);
            weatherIcon = view.findViewById(R.id.weatherIcon);
            weather = view.findViewById(R.id.weather);
            maxTemp = view.findViewById(R.id.maxTemp);
            minTemp = view.findViewById(R.id.minTemp);
            childrenRecyclerView = view.findViewById(R.id.childrenRecyclerView);
        }

    }

    public void setOnItemClickListener(OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    public WeatherForecastAdapter(Context mContext, List<Weather> itemList, HashMap<String, WeatherIcon> iconsList, final RecyclerView recyclerView, boolean isParent) {
        this.mContext = mContext;
        this.recyclerView = recyclerView;
        this.itemList = itemList;
        this.iconsList = iconsList;
        this.isParent = isParent;

        weatherFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/weathericons-regular-webfont.ttf");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_forecast_item, parent, false);
        return new WeatherForecastCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();

        Weather weather = itemList.get(adapterPosition);

        WeatherForecastCardViewHolder viewHolder = (WeatherForecastCardViewHolder) holder;

        viewHolder.dayOfWeek.setText(weather.dayOfWeek);
        viewHolder.weather.setText(weather.weather);
        viewHolder.minTemp.setText(weather.minTemp + "" + (char) 0x00B0);
        viewHolder.maxTemp.setText(weather.maxTemp + "" + (char) 0x00B0);
        viewHolder.weather.setText(weather.weather);

        viewHolder.timeOfDay.setText(weather.timeOfDay);

        viewHolder.weatherIcon.setTypeface(weatherFont);
        try {
            viewHolder.weatherIcon.setText(Utils.getWeatherIcon(mContext, iconsList, weather.weatherId));
            if(weather.weather.contains("صاف")) {
                viewHolder.weatherIcon.setTextColor(Color.parseColor("#ffdc00"));
            }
        } catch (Exception ignored) {}

        if(!isParent) {
            viewHolder.dayOfWeek.setVisibility(View.GONE);
            return;
        } else {
            viewHolder.timeOfDay.setTextSize(25);
            viewHolder.timeOfDay.setText(mContext.getString(R.string.arrow_down));
        }

        viewHolder.childrenRecyclerView.setPaddingRelative(50, 50, 50, 50);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        WeatherForecastAdapter mWeatherForecastRecyclerViewAdapter = new WeatherForecastAdapter(mContext, weather.weatherHours, iconsList, recyclerView, false);
        viewHolder.childrenRecyclerView.setLayoutManager(mLayoutManager);
        viewHolder.childrenRecyclerView.addItemDecoration(new ImprovedDividerItemDecoration(mContext, mLayoutManager.getOrientation()));
        viewHolder.childrenRecyclerView.setAdapter(mWeatherForecastRecyclerViewAdapter);

        viewHolder.itemView.setOnClickListener(v -> mOnClickListener.onClick(viewHolder.itemView, position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void toggleExpandableItem(int position) {
        for (int i = 0; i < itemList.size(); i++) {
            WeatherForecastCardViewHolder holder = (WeatherForecastCardViewHolder) recyclerView.findViewHolderForAdapterPosition(i);

            if (holder != null) {
                if(position == i) {
                    if(holder.childrenRecyclerView.getVisibility() == View.VISIBLE) {
                        holder.childrenRecyclerView.setVisibility(View.GONE);
                        holder.timeOfDay.setText(mContext.getString(R.string.arrow_down));
                    } else {
                        holder.childrenRecyclerView.setVisibility(View.VISIBLE);
                        holder.timeOfDay.setText(mContext.getString(R.string.arrow_up));
                    }
                } else {
                    holder.timeOfDay.setText(mContext.getString(R.string.arrow_down));
                    holder.childrenRecyclerView.setVisibility(View.GONE);
                }
            }
        }
    }

}