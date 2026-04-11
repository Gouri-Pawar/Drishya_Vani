package com.example.drishya_vani;

public class FavouriteModel {

    private String id;
    private String name;

    // Required empty constructor for Firebase
    public FavouriteModel(String name) {
    }

    public FavouriteModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}