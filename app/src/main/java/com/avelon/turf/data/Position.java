package com.avelon.turf.data;

public class Position {
    final public double latitude;
    final public double longitude;
    final public int zoom;


    public Position(double latitude, double longitude, int zoom) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoom = zoom;
    }

    public String toString() {
        return "latitude:" + latitude + ",longitude:" + longitude + ",zoom:" + zoom;
    }
}
