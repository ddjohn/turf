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
import com.avelon.turf.data.Zones;
import com.avelon.turf.helpers.LocationDelegate;
import com.avelon.turf.helpers.MapPermissionsDelegate;
import com.avelon.turf.helpers.Speak;
import com.avelon.turf.utils.Logger;
import com.google.android.gms.maps.GoogleMap;
import com.avelon.turf.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity {
    private Logger logger = new Logger(MapsActivity.class);

    private StateMachine states = new StateMachine();
    private StartupDialog dlg = new StartupDialog();

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private MapFragment mapFragment;
    private Speak speak;
    private Users users = new Users();
    private int gps = 0;
    ExtendedFloatingActionButton distance;

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

        mapFragment = new MapFragment(this.getSupportFragmentManager(), states);
        distance = (ExtendedFloatingActionButton)this.findViewById(R.id.distance);
        distance.setText("---- m");

        speak = new Speak(this, new Speak.Listen() {
            @Override
            public void done() {
                dlg.addMessage("Initiated speech engine");
                speak.speak("Welcome to TURF!");
            }
        });

        /*
         * Permissions
         */
        MapPermissionsDelegate mapPermissionsDelegate = new MapPermissionsDelegate(this);
        if(!mapPermissionsDelegate.checkPermissions()) {
            Toast.makeText(this, "Not enough accesses", Toast.LENGTH_LONG).show();
            return;
        }

        turfFollow();
        turfZoom();
        turfLocation();

        MyTimer t = new MyTimer();
        t.schedule(() -> runOnUiThread(() -> mapFragment.update()), 1000, 1000);
        t.schedule(() -> runOnUiThread(() -> turfUsers()),  2000, 5000);
        t.schedule(() -> runOnUiThread(() -> turfDistance()), 3000, 10000);
        t.schedule(() -> runOnUiThread(() -> turfZones()), 4000, 30000);

        //t.fireAndForget(() -> turfRegions(),  5000);
       // t.fireAndForget(() ->   turfZones(), 5000);

        dlg.show(getSupportFragmentManager(), "");
    }

    private void turfDistance() {
        mapFragment.getDistance(d -> runOnUiThread(() -> distance.setText(String.format("%2f km", d))));
    }

    private void turfFollow() {
        Follow follow = new Follow(this, (Follow.Listener) f -> states.setFollow(f));
    }

    private void turfZoom() {
        Zoom zoom = new Zoom(this, (Zoom.Listener) z -> states.setZoom(z));
    }

    private void turfLocation() {
        LocationDelegate location = new LocationDelegate((LocationManager)getSystemService(Context.LOCATION_SERVICE));
        location.register(position -> {
            logger.method("onPosition()");
            dlg.addMessage("Fount valid GPS position");
            states.setPosition(position);

            if(++gps == 6)
                dlg.cancel();
        });
    }

    private void turfUsers() {
        logger.method("turfUsers()");
        logger.info("Get users from server");

        Turf turf = new Turf(this);
        turf.request(Turf.users_location, new Turf.Listener() {
            @Override
            public void onResponse(JSONArray json) {
                logger.method("onResponse()");
                states.setUsers(json);
                dlg.addMessage("Loading " + json.length() + " users");
            }

            @Override
            public void onError(String error) {
                logger.error("That didn't work!" + error);
            }
        });
    }

    private void turfZones() {
        logger.method("turfZones()");
        logger.info("Get zones from server");

        LatLngBounds bounds = mapFragment.getBounds();
        //LatLngBounds bounds = new LatLngBounds(new LatLng(0,0), new LatLng(0, 0));

        JSONArray array = new JSONArray();
        JSONObject json = new JSONObject();
        array.put(json);

        try {
            json.put("nortEast",
                    new JSONObject().accumulate("latitude", bounds.northeast.latitude).accumulate("longitude", bounds.northeast.longitude));
            json.put("southWest",
                    new JSONObject().accumulate("latitude", bounds.southwest.latitude).accumulate("longitude", bounds.southwest.longitude));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Turf turf = new Turf(this);
        turf.request(Turf.zones, array.toString(), new Turf.Listener() {
            @Override
            public void onResponse(JSONArray json) {
                logger.method("onResponse()");
                dlg.addMessage("Loading " + json.length() + " zones");
                states.setZones(json);
            }

            @Override
            public void onError(String error) {
                logger.error("That didn't work!" + error);
                logger.error("That didn't work!" + error.toLowerCase(Locale.ROOT));
            }
        });
    }

    private void turfRegions() {
        logger.method("turfRegions()");
        logger.info("Get regions from server");

        Turf turf = new Turf(this);
        turf.request(Turf.regions, new Turf.Listener() {
            @Override
            public void onResponse(JSONArray json) {
                logger.info("region: " + json.toString());
              logger.method("turfRegions()");
        logger.info("Get regions from server");
      try {
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject obj = json.getJSONObject(i);
                        String name = obj.getString("name");
                        logger.info("region: " + obj.getString("name") + obj.getInt("id"));
                        //double latitude = obj.getDouble("latitude");
                        //double longitude = obj.getDouble("longitude");
                        //zones.add(new Zone(name, latitude, longitude));
                    }
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

/*
[{"dateCreated":"2010-08-03T00:00:00+0000","latitude":58.410839,"name":"FolkeFilbyter","id":71,"totalTakeovers":40096,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":9,"longitude":15.621597,"takeoverPoints":65},{"dateCreated":"2010-08-03T00:00:00+0000","latitude":58.411556,"name":"Larzone","id":72,"totalTakeovers":40857,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":9,"longitude":15.626026,"takeoverPoints":65},{"dateCreated":"2010-08-03T00:00:00+0000","latitude":58.411484,"name":"Domparken","id":73,"totalTakeovers":31415,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":9,"longitude":15.61872,"takeoverPoints":65},{"dateCreated":"2010-08-03T00:00:00+0000","latitude":58.407427,"name":"FountainSpot","id":74,"totalTakeovers":23711,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":8,"longitude":15.621076,"takeoverPoints":80},{"dateCreated":"2010-08-03T00:00:00+0000","latitude":58.409614,"name":"TrädSquare","id":78,"totalTakeovers":38927,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":9,"longitude":15.624386,"takeoverPoints":65},{"dateCreated":"2010-08-04T00:00:00+0000","latitude":58.406592,"name":"VallaCircle","id":79,"totalTakeovers":17311,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":6,"longitude":15.582949,"takeoverPoints":110},{"dateCreated":"2010-08-04T00:00:00+0000","latitude":58.413119,"name":"SoccerInWoods","id":80,"totalTakeovers":9622,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":6,"longitude":15.584758,"takeoverPoints":110},{"dateCreated":"2010-08-03T00:00:00+0000","latitude":58.410229,"name":"LillaTorget","id":81,"totalTakeovers":38194,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":9,"longitude":15.626568,"takeoverPoints":65},{"dateCreated":"2010-08-04T00:00:00+0000","latitude":58.409633,"name":"HG","id":82,"totalTakeovers":9125,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":6,"longitude":15.563393,"takeoverPoints":110},{"dateCreated":"2010-08-03T00:00:00+0000","latitude":58.41621,"name":"Resecentrum","id":83,"totalTakeovers":25757,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":9,"longitude":15.626087,"takeoverPoints":65},{"dateCreated":"2010-08-03T00:00:00+0000","latitude":58.404359,"name":"Tornet","id":84,"totalTakeovers":10304,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":6,"longitude":15.620379,"takeoverPoints":110},{"dateCreated":"2010-08-03T00:00:00+0000","latitude":58.411636,"name":"Hunnecross","id":85,"totalTakeovers":28422,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":8,"longitude":15.612442,"takeoverPoints":80},{"dateCreated":"2010-08-03T00:00:00+0000","latitude":58.408279,"name":"Watertower","id":86,"totalTakeovers":13141,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":6,"longitude":15.611257,"takeoverPoints":110},{"dateCreated":"2010-08-03T00:00:00+0000","latitude":58.398947,"name":"Bergatornet","id":87,"totalTakeovers":8441,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":6,"longitude":15.634849,"takeoverPoints":110},{"dateCreated":"2010-08-03T00:00:00+0000","latitude":58.401896,"name":"Emergenzone","id":88,"totalTakeovers":13741,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":6,"longitude":15.623249,"takeoverPoints":110},{"dateCreated":"2010-09-04T14:41:37+0000","latitude":58.405062,"name":"Linklake","id":89,"totalTakeovers":2204,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":4,"longitude":15.631782,"takeoverPoints":140},{"dateCreated":"2010-08-03T00:00:00+0000","latitude":58.4008,"name":"Brohuset","id":90,"totalTakeovers":6585,"region":{"country":"se","name":"Östergötland","id":139},"pointsPerHour":5,"longitude":15.64662,"takeoverPoints":125},{"dateCreated":"2010-08-04T00:00:00+0000","latitude":58
 */
/*
2022-01-26 19:48:33.040 20687-20687/com.avelon.turf E/AndroidRuntime: FATAL EXCEPTION: main
        Process: com.avelon.turf, PID: 20687
        java.lang.OutOfMemoryError: Failed to allocate a 18874376 byte allocation with 4573968 free bytes and 4466KB until OOM, target footprint 268435456, growth limit 268435456
        at java.util.Arrays.copyOf(Arrays.java:3257)
        at java.lang.AbstractStringBuilder.ensureCapacityInternal(AbstractStringBuilder.java:124)
        at java.lang.AbstractStringBuilder.append(AbstractStringBuilder.java:649)
        at java.lang.StringBuilder.append(StringBuilder.java:203)
        at org.json.JSONStringer.string(JSONStringer.java:354)
        at org.json.JSONStringer.value(JSONStringer.java:261)
        at org.json.JSONObject.writeTo(JSONObject.java:734)
        at org.json.JSONStringer.value(JSONStringer.java:246)
        at org.json.JSONArray.writeTo(JSONArray.java:616)
        at org.json.JSONArray.toString(JSONArray.java:587)
        at com.avelon.turf.MapsActivity$6.onResponse(MapsActivity.java:171)
        at com.avelon.turf.Turf.lambda$request$0$Turf(Turf.java:44)
*/