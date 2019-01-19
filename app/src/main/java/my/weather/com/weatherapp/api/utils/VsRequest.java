package my.weather.com.weatherapp.api.utils;

import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/** A request for retrieving a {@link JSONObject} response body at a given URL. */
public class VsRequest extends JsonRequest<String> {

    /**
     * Creates a new request.
     *
     * @param url URL to fetch the JSON from
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public VsRequest(String url, Listener<String> listener, @Nullable ErrorListener errorListener) {
        super(Method.GET, url, null, listener, errorListener);
    }

    /**
     * Creates a new request.
     *
     * @param method the HTTP method to use
     * @param url URL to fetch the JSON from
     * @param jsonRequest A {@link JSONObject} to post with the request. Null indicates no parameters
     *     will be posted along with request.
     * @param listener Listener to receive the {@link JSONObject} response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public VsRequest(int method, String url, @Nullable JSONObject jsonRequest, Listener<String> listener, @Nullable ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
    }

    /**
     * Creates a new request.
     *
     * @param method the HTTP method to use
     * @param url URL to fetch the JSON from
     * @param jsonRequest A {@link JSONArray} to post with the request. Null indicates no parameters
     *     will be posted along with request.
     * @param listener Listener to receive the {@link JSONObject} response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public VsRequest(int method, String url, @Nullable JSONArray jsonRequest, Listener<String> listener, @Nullable ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();

        return headers;
    }

}

