package com.example.drishya_vani;

public class Landmark {

    String name;
    double latitude;
    double longitude;
    String description;
    int imageResId;

    public Landmark(String name, double latitude, double longitude, String description, int imageResId) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }
}