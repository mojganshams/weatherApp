package my.weather.com.weatherapp.api.utils;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppVolleyClient {
    private static AppVolleyClient mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private AppVolleyClient(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized AppVolleyClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppVolleyClient(context);
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        getRequestQueue().add(req);
    }
}