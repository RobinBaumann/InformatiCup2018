package com.github.robinbaumann.informaticup2018.model;


import java.util.List;

public class RouteRequest {
    private int capacity;

    private List<RoutePoint> routePoints;

    //empty ctor for libs
    public RouteRequest() {}

    public RouteRequest(int capacity, List<RoutePoint> routePoints) {
        this.capacity = capacity;
        this.routePoints = routePoints;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<RoutePoint> getRoutePoints() {
        return routePoints;
    }
}
