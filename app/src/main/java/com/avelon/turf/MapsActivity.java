package com.avelon.turf;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.avelon.turf.data.User;
import com.avelon.turf.data.Zone;
import com.google.android.gms.maps.GoogleMap;
import com.avelon.turf.databinding.ActivityMapsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity {
    private Logger logger = new Logger(MapsActivity.class);

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private MapFragment mapFragment;
    private MapPermissionsDelegate mapPermissionsDelegate;
    private Speak speak;

    @Override
    protected void onDestroy() {
        logger.method("onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        logger.method("onPause()");
        super.onPause();
    }

    @Override
    protected void onResume() {
        logger.method("onResume()");
        super.onResume();
    }

    @Override
    protected void onStart() {
        logger.method("onStart()");
        super.onStart();
    }

    @Override
    protected void onStop() {
        logger.method("onStop()");
        super.onStop();
    }

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
        speak = new Speak(this, new Speak.Listen() {
            @Override
            public void done() {
                speak.speak("Welcome to TURF!");
            }
        });

        /*
         * Permissions
         */
        if (mapPermissionsDelegate.checkPermissions()) {
        } else {
            Toast.makeText(this, "Not enough accesses", Toast.LENGTH_LONG).show();
            return;
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                turfUsers();
            }
        }, 10000, 10*10000);

        Timer timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                turfZones();
            }
        }, 5000, 3600*1000);

        locationMethod();
    }

    private void locationMethod() {
        LocationDelegate location = new LocationDelegate((LocationManager)getSystemService(Context.LOCATION_SERVICE));
        location.register(location1 -> mapFragment.update(location1.getLatitude(), location1.getLongitude(), 10 /*15*/));
    }
    private void turfUsers() {
        logger.method("turfUsers()");

        Turf turf = new Turf(this);
        turf.request(Turf.users_location, new Turf.Listener() {
            @Override
            public void onResponse(JSONArray json) {
                try {
                    List<User> users = new ArrayList<User>();
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject obj = json.getJSONObject(i);
                        String name = obj.getString("name");
                        double latitude = obj.getDouble("latitude");
                        double longitude = obj.getDouble("longitude");
                        users.add(new User(name, latitude, longitude));
                    }
                    mapFragment.setUsers(users);
                } catch (JSONException e) {
                    logger.error("" + e);
                }
            }

            @Override
            public void onError(String error) {
                logger.error("That didn't work!" + error);
            }
        });
    }

    private void turfZones() {
        logger.method("turfZones()");

        Turf turf = new Turf(this);
        turf.request(Turf.zones_all, new Turf.Listener() {
            @Override
            public void onResponse(JSONArray json) {
                logger.error(json.toString());
                try {
                    List<Zone> zones = new ArrayList<Zone>();
                    for(int i = 0; i < json.length(); i++) {
                        JSONObject obj = json.getJSONObject(i);
                        String name = obj.getString("name");
                        double latitude = obj.getDouble("latitude");
                        double longitude = obj.getDouble("longitude");
                        zones.add(new Zone(name, latitude, longitude));
                    }
                    mapFragment.setZones(zones);
                }
                catch(JSONException e) {
                    logger.error("" + e);
                }
            }

            @Override
            public void onError(String error) {
                logger.error("That didn't work!" + error);
            }
        });
    }

    @Override
    public void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        logger.method("onActivityResult()", request, result, data.toString());
        speak.onActivityResult(request, result, data);
    }
}