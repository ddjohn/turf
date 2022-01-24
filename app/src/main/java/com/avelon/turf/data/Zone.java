package com.avelon.turf.data;

public class Zone {
    final public String name;
    final public double latitude;
    final public double longitude;

    public Zone(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String toString() {
        return name;
    }
}
