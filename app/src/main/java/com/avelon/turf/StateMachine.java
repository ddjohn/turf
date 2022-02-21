package com.avelon.turf;

import android.location.Location;

import androidx.annotation.NonNull;

import com.avelon.turf.data.Position;
import com.avelon.turf.data.User;
import com.avelon.turf.data.Users;
import com.avelon.turf.data.Zone;
import com.avelon.turf.data.Zones;
import com.avelon.turf.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StateMachine {
    private static final Logger logger = new Logger(StateMachine.class);

    public Zones zones = new Zones();
    public Users users = new Users();
    public Position position = new Position(0, 0);
    public boolean follow = true;
    public int zoom = 0;
    public boolean draw = false;

    public void setZones(@NonNull JSONArray json) {
        try {
            for(int i = 0; i < json.length(); i++) {
                JSONObject obj = json.getJSONObject(i);
                String name = obj.getString("name");
                double latitude = obj.getDouble("latitude");
                double longitude = obj.getDouble("longitude");
                zones.add(new Zone(name, latitude, longitude));
            }
        }
        catch(JSONException e) {
            logger.error("" + e);
        }
        draw = true;
    }

    public void setUsers(@NonNull JSONArray json) {
        try {
            List<User> tempUsers = new ArrayList<User>();
            for (int i = 0; i < json.length(); i++) {
                JSONObject obj = json.getJSONObject(i);
                String name = obj.getString("name");
                double latitude = obj.getDouble("latitude");
                double longitude = obj.getDouble("longitude");
                tempUsers.add(new User(name, latitude, longitude));
            }
            users.setUsers(tempUsers);
        }
        catch (JSONException e) {
            logger.error("" + e);
        }
        draw = true;
    }

    public void setPosition(Location location) {
        position = new Position(location.getLatitude(), location.getLongitude());
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
        draw = true;
    }
}
