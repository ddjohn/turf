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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MapFragment implements OnMapReadyCallback {
    private static final Logger logger = new Logger(MapFragment.class);

    private StateMachine states;
    private GoogleMap map;

    private final HashMap<String, Marker> markers = new HashMap<>();
    private Marker meMarker = null;

    public MapFragment(FragmentManager mgr, StateMachine states) {
        super();
        logger.method("MapFragment()");

        this.states = states;

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
        if(states.draw == false)
            return;

        LatLng here = new LatLng(states.position.latitude, states.position.longitude);
        if(states.follow) {
            CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(here, states.zoom);
            map.moveCamera(camera);
        }
        //map.clear();

        logger.info("Drawing " + states.users.size() + " users");
        for(User user : states.users.values()) {
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

        logger.info("Drawing " + states.zones.size() + " zones");
        for(Zone zone : states.zones) {
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
        states.draw = false;

    }

    public void getDistance(DistantListener listener) {
        double distance = 999999;
        for(User user : states.users.values()) {
            double meter = /*1.609344* */GFG.distance(user.latitude, states.position.latitude, user.longitude, states.position.longitude);
            logger.debug(user.name + "<:>" + meter + " meter");
            if(meter < distance) {
                distance = meter;
            }
        }
        logger.info("<:>Closest " + distance);
        listener.onDistance(distance);
    }

    public LatLngBounds getBounds() {
        return map.getProjection().getVisibleRegion().latLngBounds;
    }

    public interface DistantListener {
        void onDistance(double distance);
    }
}
