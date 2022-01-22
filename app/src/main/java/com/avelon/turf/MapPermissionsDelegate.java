package com.avelon.turf;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class MapPermissionsDelegate implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static Logger logger = new Logger(MapPermissionsDelegate.class);

    private static final int REQUEST_CODE_PERMISSION = 666;

    private static final String[] permissions = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private final Activity activity;

    public MapPermissionsDelegate(Activity activity) {
        logger.method("MapPermissionsDelegate()");
        this.activity = activity;
    }

    public boolean checkPermissions() {
        logger.method("checkPermissions()");

        for(String permission : permissions) {
            if(ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE_PERMISSION);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] permissions, @NonNull int[] results) {
        logger.method("onRequestPermissionsResult()", code, permissions, results);
        activity.onRequestPermissionsResult(code, permissions, results);
    }
}
