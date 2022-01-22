package com.avelon.turf;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment implements OnMapReadyCallback {
    private static final Logger logger = new Logger(MapFragment.class);

    private GoogleMap map;

    public MapFragment(FragmentManager mgr) {
        super();
        logger.method("MapFragment()", mgr);

        SupportMapFragment mapFragment = (SupportMapFragment)mgr.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        logger.method("onMapReady()", map);
        this.map = map;
    }

    public void update(double latitude, double longitude, int zoom) {
        logger.method("update()", latitude, longitude, zoom);
        LatLng here = new LatLng(latitude, longitude);
        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(here, zoom); // 2-21
        map.moveCamera(camera);
        map.clear();
        map.addMarker(new MarkerOptions().position(here).title("Me")).showInfoWindow();

    }
}
