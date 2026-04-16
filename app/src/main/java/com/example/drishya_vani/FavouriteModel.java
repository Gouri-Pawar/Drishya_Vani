package com.example.drishya_vani;

public class FavouriteModel {

    private String key;
    private String name;
    private String type;
    private double lat;
    private double lon;
    private double distance;

    public FavouriteModel(String key, String name, String type,
                          double lat, double lon, double distance) {
        this.key = key;
        this.name = name;
        this.type = type;
        this.lat = lat;
        this.lon = lon;
        this.distance = distance;
    }

    public String getKey()      { return key; }
    public String getName()     { return name; }
    public String getType()     { return type; }
    public double getLat()      { return lat; }
    public double getLon()      { return lon; }
    public double getDistance() { return distance; }

    // universal key generator ⭐
    public static String generateKey(String name,double lat,double lon)
    {
        return name.replace(" ","_") + "_" + lat + "_" + lon;
    }
}