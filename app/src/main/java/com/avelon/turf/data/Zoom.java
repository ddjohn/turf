package com.avelon.turf.data;

import android.app.Activity;
import android.view.View;

import com.avelon.turf.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Zoom {
    public Zoom(Activity activity, Listener listener) {
        FloatingActionButton world = (FloatingActionButton) activity.findViewById(R.id.world);
        FloatingActionButton city = (FloatingActionButton) activity.findViewById(R.id.city);
        FloatingActionButton street = (FloatingActionButton) activity.findViewById(R.id.street);

        world.setOnClickListener(v -> listener.onChange(0));
        city.setOnClickListener(v -> listener.onChange(10));
        street.setOnClickListener(v -> listener.onChange(14));
    }

    public interface Listener {
        public void onChange(int zoom);
    }
}
