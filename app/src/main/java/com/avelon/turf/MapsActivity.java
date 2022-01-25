package com.avelon.turf;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.avelon.turf.buttons.Follow;
import com.avelon.turf.data.Position;
import com.avelon.turf.data.User;
import com.avelon.turf.data.Users;
import com.avelon.turf.data.Zone;
import com.avelon.turf.buttons.Zoom;
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
    private Speak speak;
    private Users users = new Users();

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        logger.method("onSaveInstanceState()");

        bundle.putInt("test", 7);
        logger.error("bundler-save: " + bundle);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        logger.method("onDestroy()");
        super.onCreate(bundle);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*
         * Components
         */
        mapFragment = new MapFragment(this.getSupportFragmentManager());
        MapPermissionsDelegate mapPermissionsDelegate = new MapPermissionsDelegate(this);
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

        turfFollow();
        turfZoom();
        turfLocation();


        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                logger.info("Get users from server");
                turfUsers();
            }
        }, 0, 3*1000);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                logger.info("Get zones from server");
                turfZones();
            }
        }, 5000, 3600*1000);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> mapFragment.update());
            }
        }, 1000, 1000);

    }

    private void turfFollow() {
        Follow follow = new Follow(this, (Follow.Listener) follow1 -> mapFragment.setFollow(follow1));
    }

    private void turfZoom() {
        Zoom zoom = new Zoom(this, (Zoom.Listener) zoom1 -> mapFragment.setZoom(zoom1));
    }

    private void turfLocation() {
        LocationDelegate location = new LocationDelegate((LocationManager)getSystemService(Context.LOCATION_SERVICE));
        location.register(position -> {
            mapFragment.setPosition(new Position(position.getLatitude(), position.getLongitude()));
        });
    }

    private void turfUsers() {
        logger.method("turfUsers()");

        Turf turf = new Turf(this);
        turf.request(Turf.users_location, new Turf.Listener() {
            @Override
            public void onResponse(JSONArray json) {
                try {
                    List<User> tempUsers = new ArrayList<User>();
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject obj = json.getJSONObject(i);
                        String name = obj.getString("name");
                        double latitude = obj.getDouble("latitude");
                        double longitude = obj.getDouble("longitude");
                        tempUsers.add(new User(name, latitude, longitude));
                    }
                    users.setUsers(tempUsers);
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
                logger.info(json.toString());
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