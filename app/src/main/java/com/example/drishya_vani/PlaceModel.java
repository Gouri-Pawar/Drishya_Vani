package com.example.drishya_vani;

import java.util.List;

public class PlaceModel {

    String name;
    List<String> visits;
    public boolean isFavourite = false;

    public PlaceModel(String name, List<String> visits) {
        this.name = name;
        this.visits = visits;
    }

    public String getName() {
        return name;
    }

    public List<String> getVisits() {
        return visits;
    }
}