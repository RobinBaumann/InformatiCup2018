package Model;


import java.util.List;

public class RouteRequest {
    private int capacity;

    private List<RoutePoint> routePoints;

    public int getCapacity() {
        return capacity;
    }

    public List<RoutePoint> getRoutePoints() {
        return routePoints;
    }
}
