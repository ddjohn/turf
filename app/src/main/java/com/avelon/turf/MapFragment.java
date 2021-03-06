package com.avelon.turf;

import androidx.fragment.app.FragmentManager;
import com.avelon.turf.data.Position;
import com.avelon.turf.data.User;
import com.avelon.turf.data.Users;
import com.avelon.turf.data.Zone;
import com.avelon.turf.data.Zones;
import com.avelon.turf.helpers.Speak;
import com.avelon.turf.utils.Logger;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MapFragment implements OnMapReadyCallback {
    private static final Logger logger = new Logger(MapFragment.class);

    private GoogleMap map;

    private Users users = new Users();
    private Zones zones = new Zones();
    private Position position = new Position(0.0, 0.0);
    private final HashMap<String, Marker> markers = new HashMap<>();
    private Marker meMarker = null;

    private int zoom = 0;
    private boolean draw = false;
    private boolean follow = true;

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

    public synchronized void update() {
        logger.method("update()");
        if(draw == false)
            return;

        LatLng here = new LatLng(position.latitude, position.longitude);
        if(follow) {
            CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(here, zoom);
            map.moveCamera(camera);
        }
        //map.clear();

        logger.info("Drawing " + users.size() + " users");
        for(User user : users.values()) {
            LatLng position = new LatLng(user.latitude, user.longitude);

            if (markers.containsKey(user.name)) {
                Marker marker = markers.get(user.name);
                logger.debug("Move marker to for " + user.name + " to " + position);
                marker.setPosition(position);
                if(user.hidden) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                } else {
                    marker.setIcon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }
                markers.put(user.name, marker);
            } else {
                logger.debug("Create new marker for " + user.name + " to " + position);
                MarkerOptions markerOptions;
                if(user.hidden) {
                    markerOptions = new MarkerOptions().position(position).title(user.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                } else {
                    markerOptions = new MarkerOptions().position(position).title(user.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                }
                Marker marker = map.addMarker(markerOptions);
                //marker.showInfoWindow();
                markers.put(user.name, marker);
            }
        }

        logger.info("Drawing " + zones.size() + " zones");
        for(Zone zone : zones) {
            LatLng position = new LatLng(zone.latitude, zone.longitude);
            map.addCircle(new CircleOptions().center(position));
        }

        logger.info("Drawing me");
        if(meMarker == null) {
            meMarker = map.addMarker(new MarkerOptions().position(here).title("Me"));
            meMarker.showInfoWindow();
        } else {
            meMarker.setPosition(here);
        }
        draw = false;
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

    public synchronized void setZones(Zones zones) {
        logger.method("setZones()", users);
        this.zones = zones;
        draw = true;
    }

    public void setZoom(int zoom) {
        logger.method("setZoom()", zoom);
        this.zoom = zoom;
        draw = true;
    }

    public void setFollow(boolean follow) {
        logger.method("setFollow()", follow);
        this.follow = follow;
    }

    public void getDistance(DistantListener listener) {
        double distance = 999999;
        for(User user : users.values()) {
            double meter = /*1.609344* */GFG.distance(user.latitude, position.latitude, user.longitude, position.longitude);
            logger.debug(user.name + "<:>" + meter + " meter");
            if(meter < distance) {
                distance = meter;
            }
        }
        logger.error("<:>Closest " + distance);
        listener.onDistance(distance);
    }

    public interface DistantListener {
        void onDistance(double distance);
    }
}
