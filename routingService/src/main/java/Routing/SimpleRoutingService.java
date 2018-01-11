package Routing;

import Model.RouteRequest;
import Model.RouteResponse;

public class SimpleRoutingService {
    private final static double STARTING_AMOUNT = 0;

    public RouteResponse route(RouteRequest request) {
        FixedGasStation.calculateRoute(null, 0,0); //TODO fix
        return null; //TODO fix
    }
}
