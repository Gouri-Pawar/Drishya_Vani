package com.example.drishya_vani;

public class PlaceModel {

    String name;
    String description;
    String date;

    public PlaceModel(String name, String description, String date) {
        this.name = name;
        this.description = description;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }
}
