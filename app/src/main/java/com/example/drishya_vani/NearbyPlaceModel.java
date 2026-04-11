package com.example.drishya_vani;

public class NearbyPlaceModel {

    private String name;
    private double lat;
    private double lon;
    private String type;      // tourism / amenity / historic
    private double distance;  // in meters from user

    public NearbyPlaceModel(String name, double lat, double lon, String type, double distance) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
        this.distance = distance;
    }

    public String getName()     { return name; }
    public double getLat()      { return lat; }
    public double getLon()      { return lon; }
    public String getType()     { return type; }
    public double getDistance() { return distance; }

    // For FavouriteManager — unique key per place
    public String getKey() {
        return name + "_" + lat + "_" + lon;
    }
}