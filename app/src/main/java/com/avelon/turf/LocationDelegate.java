package com.avelon.turf;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;

public class LocationDelegate {
    private Logger logger = new Logger(LocationDelegate.class);

    private static final int MIN_FREQUENCY = 1000;
    private static final float MIN_DISTANCE = 1.0f;

    private LocationManager manager;

    public LocationDelegate(LocationManager manager) {
        this.manager = manager;
    }

    @SuppressLint("MissingPermission")
    public void register(Listen listen) {
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_FREQUENCY, MIN_DISTANCE, location -> {
            logger.method("onLocationUpdate()", location);
            listen.onLocationChanged(location);
        });
    }

    public interface Listen {
        public void onLocationChanged(@NonNull Location location);
    }
}
