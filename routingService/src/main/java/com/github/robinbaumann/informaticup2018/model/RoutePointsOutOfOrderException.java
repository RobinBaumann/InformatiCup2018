package com.github.robinbaumann.informaticup2018.model;

public class RoutePointsOutOfOrderException extends Exception {
    private int i;

    public RoutePointsOutOfOrderException(int i) {
        this.i = i;
    }

    public int getI() {
        return i;
    }
}
