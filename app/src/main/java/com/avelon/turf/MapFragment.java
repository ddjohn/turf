package com.avelon.turf;

import androidx.fragment.app.FragmentManager;

import com.avelon.turf.data.User;
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

public class MapFragment implements OnMapReadyCallback {
    private static final Logger logger = new Logger(MapFragment.class);

    private GoogleMap map;

    private List<User> users = new ArrayList<User>();
    private List<Zone> zones = new ArrayList<Zone>();

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

    public void update(double latitude, double longitude, int zoom) {
        logger.method("update()", latitude, longitude, zoom);

        LatLng here = new LatLng(latitude, longitude);
        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(here, zoom); // 2-21
        map.moveCamera(camera);

        map.clear();

        logger.info("Drawing " + users.size() + " users");
        for(User user : users) {
            LatLng position = new LatLng(user.latitude, user.longitude);
            map.addMarker(
                    new MarkerOptions()
                            .position(position).title(user.name)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
        logger.info("Drawing " + zones.size() + " zones");
        for(Zone zone : zones) {
            LatLng position = new LatLng(zone.latitude, zone.longitude);
            map.addCircle(new CircleOptions().center(position));
        }
        logger.info("Drawing me");
        map.addMarker(new MarkerOptions().position(here).title("Me")).showInfoWindow();
    }

    public void setUsers(List<User> users) {
        logger.method("setUsers()", users);
        this.users = users;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }
}
