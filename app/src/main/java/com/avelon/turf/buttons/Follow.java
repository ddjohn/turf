package com.avelon.turf.buttons;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;

import com.avelon.turf.utils.Logger;
import com.avelon.turf.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Follow {
    private Logger logger = new Logger(Follow.class);
    private boolean follow = true;

    public Follow(Activity activity, Listener listener) {
        logger.method("Follow");

        FloatingActionButton followButton = (FloatingActionButton)activity.findViewById(R.id.follow);

        followButton.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        listener.onChange(follow);

        followButton.setOnClickListener(v -> {
            follow = !follow;
            followButton.setBackgroundTintList(ColorStateList.valueOf(follow ? Color.GRAY : Color.LTGRAY));
            listener.onChange(follow);
        });
    }

    public interface Listener {
        public void onChange(boolean follow);
    }
}
