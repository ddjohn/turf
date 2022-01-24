package com.avelon.turf.data;

public class Position {
    final public double latitude;
    final public double longitude;

    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String toString() {
        return "latitude:" + latitude + ",longitude:" + longitude;
    }
}
