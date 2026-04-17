package com.example.drishya_vani;

import java.util.List;

public class PlaceModel {

    String placeName;
    List<String> visits;

    public PlaceModel() {}

    public PlaceModel(String placeName, List<String> visits) {
        this.placeName = placeName;
        this.visits = visits;
    }

    public String getPlaceName() {
        return placeName;
    }

    public List<String> getVisits() {
        return visits;
    }
}