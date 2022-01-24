package com.avelon.turf.data;

public class User {
    final public String name;
    final public double latitude;
    final public double longitude;
    public boolean hidden;

    public User(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hidden = false;
    }

    public String toString() {
        return "name:" + name + ",latitude:" + latitude + ",longitude:" + longitude + ",hidden:" + hidden;
    }
}
