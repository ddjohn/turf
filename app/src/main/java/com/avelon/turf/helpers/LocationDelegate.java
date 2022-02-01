package com.avelon.turf.helpers;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import androidx.annotation.NonNull;

import com.avelon.turf.utils.Logger;

public class LocationDelegate {
    private Logger logger = new Logger(LocationDelegate.class);

    private static final int MIN_FREQUENCY = 1000;
    private static final float MIN_DISTANCE = 0; //1.0f;

    private LocationManager manager;

    public LocationDelegate(LocationManager manager) {
        this.manager = manager;
    }

    @SuppressLint("MissingPermission")
    public void register(Listen listen) {
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_FREQUENCY, MIN_DISTANCE, location -> {
            logger.info("Got a new position:" + location);
            listen.onLocationChanged(location);
        });
    }

    public interface Listen {
        public void onLocationChanged(@NonNull Location location);
    }
}
