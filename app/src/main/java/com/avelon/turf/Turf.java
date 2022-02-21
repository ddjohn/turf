package com.avelon.turf;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.avelon.turf.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Turf {
    private Logger logger = new Logger(Turf.class);

    public static String feeds = "v4/feeds"; // Parse error
    public static String statistics = "v4/statistics";
    public static String rounds = "v4/rounds"; // Parse error
    public static String users = "v4/users"; //Post?
    public static String users_location = "v4/users/location"; // Parser error
    public static String users_top = "v4/users/top"; //Parse error
    public static String regions = "v4/regions"; // Parser error
    public static String zones = "v4/zones"; // Post
    public static String zones_all = "v4/zones/all"; // Post

    private RequestQueue queue;

    public Turf(Context ctx) {
        queue = Volley.newRequestQueue(ctx);
    }

    public void request(String url, Listener listener) {
        logger.method("get_request");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api.turfgame.com/" + url,
                response -> {
                    try {
                        logger.method("onResponse()");
                        JSONArray obj = new JSONArray(response);
                        listener.onResponse(obj);
                    }
                    catch(JSONException e) {
                        logger.error("Parse error: " + url + " " + e.toString());
                        listener.onError("Parse error: " + e.toString());
                    }
                }, error -> {
                    logger.error("Network error: "  + url + " " + error);
                    listener.onError("Network error: " + error);
                }
        );
        queue.add(stringRequest);
    }

    public void request(String url, String json, Listener listener) {
        logger.method("post_request", json, url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.turfgame.com/" + url,
                response -> {
                    logger.method("onResponse()", response);
                    try {
                        JSONArray obj = new JSONArray(response);
                        listener.onResponse(obj);
                    }
                    catch (JSONException e) {
                        logger.error("Parse error: " + url + " " + e.toString());
                        listener.onError("Parse error: " + e.toString());
                    }
                }, error -> {
            logger.error("Network error: "  + url + " " + error);
            listener.onError("Network error: " + error);
        }
        ) {

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return json.getBytes();
            }
        };
        queue.add(stringRequest);
    }

    public interface Listener {
        public void onResponse(JSONArray response);
        public void onError(String error);
    }
}
