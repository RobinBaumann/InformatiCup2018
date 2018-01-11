package Model;

import Validation.JsonRequired;

public class RouteRequest {
    @JsonRequired
    private int capacity;

    @JsonRequired
    private RoutePoint[] routePoints;

    public int getCapacity() {
        return capacity;
    }

    public RoutePoint[] getRoutePoints() {
        return routePoints;
    }
}
