package Model;

import Validation.JsonRequired;

import java.util.List;

public class RouteRequest {
    @JsonRequired
    private int capacity;

    @JsonRequired
    private List<RoutePoint> routePoints;

    public int getCapacity() {
        return capacity;
    }

    public List<RoutePoint> getRoutePoints() {
        return routePoints;
    }
}
