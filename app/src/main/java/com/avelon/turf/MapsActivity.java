package com.avelon.turf;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.avelon.turf.databinding.ActivityMapsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MapsActivity extends FragmentActivity {
    private Logger logger = new Logger(MapsActivity.class);

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private MapFragment mapFragment;
    private MapPermissionsDelegate mapPermissionsDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*
         * Components
         */
        mapFragment = new MapFragment(this.getSupportFragmentManager());
        mapPermissionsDelegate = new MapPermissionsDelegate(this);

        /*
         * Permissions
         */
        if (mapPermissionsDelegate.checkPermissions()) {
        } else {
            Toast.makeText(this, "Not enough accesses", Toast.LENGTH_LONG).show();
            return;
        }

        turfMethod();
        locationMethod();
    }

    private void locationMethod() {
        LocationDelegate location = new LocationDelegate((LocationManager)getSystemService(Context.LOCATION_SERVICE));
        location.register(new LocationDelegate.Listen() {

            @Override
            public void onLocationChanged(Location location) {
                mapFragment.update(location.getLatitude(), location.getLongitude(), 15);
            }
        });
    }

    private void turfMethod() {
        Turf turf = new Turf(this);
        turf.request(Turf.rounds, new Turf.Listener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    logger.info("Response is: " + response.getString("totalUsers"));
                }
                catch(JSONException e) {
                    logger.error("" + e);
                }
            }

            @Override
            public void onParseError(String error) {
                logger.error("That didn't work!" + error);
            }

            @Override
            public void onError(VolleyError error) {
                logger.error("That didn't work!" + error);
            }
        });
    }
}