package com.avelon.turf.buttons;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;

import com.avelon.turf.Logger;
import com.avelon.turf.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Zoom {
    private Logger logger = new Logger(Zoom.class);

    public Zoom(Activity activity, Listener listener) {
        logger.method("Zoom");
        FloatingActionButton world = (FloatingActionButton) activity.findViewById(R.id.world);
        FloatingActionButton city = (FloatingActionButton) activity.findViewById(R.id.city);
        FloatingActionButton street = (FloatingActionButton) activity.findViewById(R.id.street);

        world.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        city.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        street.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        listener.onChange(0);

        world.setOnClickListener(v -> {
            world.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            city.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            street.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            listener.onChange(0);
        });
        city.setOnClickListener(v -> {
            world.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            city.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            street.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            listener.onChange(11);
        });
        street.setOnClickListener(v -> {
            world.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            city.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            street.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            listener.onChange(15);
        });
    }

    public interface Listener {
        public void onChange(int zoom);
    }
}
