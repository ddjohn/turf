package com.avelon.turf;

import androidx.fragment.app.FragmentManager;

import com.avelon.turf.data.Position;
import com.avelon.turf.data.User;
import com.avelon.turf.data.Users;
import com.avelon.turf.data.Zone;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapFragment implements OnMapReadyCallback {
    private static final Logger logger = new Logger(MapFragment.class);

    private boolean draw = false;

    private GoogleMap map;

    private Users users = new Users();
    private List<Zone> zones = new ArrayList<Zone>();
    private Position position = new Position(0,0,0);

    public MapFragment(FragmentManager mgr) {
        super();
        logger.method("MapFragment()");

        SupportMapFragment mapFragment = (SupportMapFragment)mgr.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        logger.method("onMapReady()");
        this.map = map;
    }

    public void update() {
        logger.method("update()");

        LatLng here = new LatLng(position.latitude, position.longitude);
        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(here, position.zoom); // 2-21
        map.moveCamera(camera);

        map.clear();

        logger.info("Drawing " + users.size() + " users");
        for(User user : users.values()) {
            LatLng position = new LatLng(user.latitude, user.longitude);
            if(user.hidden) {
                map.addMarker(new MarkerOptions().position(position).title(user.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))).showInfoWindow();
            } else {
                map.addMarker(new MarkerOptions().position(position).title(user.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).showInfoWindow();
            }
        }

        logger.info("Drawing " + zones.size() + " zones");
        for(Zone zone : zones) {
            LatLng position = new LatLng(zone.latitude, zone.longitude);
            map.addCircle(new CircleOptions().center(position));
        }

        logger.info("Drawing me");
        map.addMarker(new MarkerOptions().position(here).title("Me")).showInfoWindow();
    }

    public synchronized void setPosition(Position position) {
        logger.method("setPosition()", position);
        this.position = position;
        draw = true;
    }

    public synchronized void setUsers(Users users) {
        logger.method("setUsers()", users);
        this.users = users;
        draw = true;
    }

    public synchronized void setZones(List<Zone> zones) {
        logger.method("setZones()", users);
        this.zones = zones;
        draw = true;
    }
}
