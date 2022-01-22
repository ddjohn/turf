package com.avelon.turf;

public class User {
    final String name;
    final double latitude;
    final double longitude;

    public User(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String toString() {
        return name;
    }
}
