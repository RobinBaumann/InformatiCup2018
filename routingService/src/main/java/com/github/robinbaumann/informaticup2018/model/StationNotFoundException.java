package com.github.robinbaumann.informaticup2018.model;

public class StationNotFoundException extends Exception {
    private int id;

    public StationNotFoundException(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
