package Model;

import Validation.JsonRequired;

public class RouteRequest {
    @JsonRequired
    int capacity;

    @JsonRequired
    RoutePoint[] routePoints;
}
