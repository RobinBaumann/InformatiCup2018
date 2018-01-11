package Routing;

import GasStation.GasStation;
import Model.*;

import java.time.OffsetDateTime;
import java.util.LinkedList;

public class SimpleRoutingService {
    private final static double STARTING_AMOUNT = 0;

    public GasStrategy route(RouteRequest request) throws EmptyRouteException, RoutePointsOutOfOrderException, CapacityException {
        validate(request);
        LinkedList<GasStation> stations = new LinkedList<>();

        FixedGasStation.calculateRoute(stations, 0, STARTING_AMOUNT); //TODO fix
        return null; //TODO fix
    }

    private void validate(RouteRequest request) throws CapacityException, EmptyRouteException, RoutePointsOutOfOrderException {
        if (request.getCapacity() < 1) {
            throw new CapacityException(request.getCapacity());
        }
        if (request.getRoutePoints().length < 1) {
            throw new EmptyRouteException();
        }
        OffsetDateTime lastOffset = request.getRoutePoints()[0].getTimestamp();
        for (int i = 1; i < request.getRoutePoints().length; i++) {
            if (request.getRoutePoints()[i].getTimestamp().compareTo(lastOffset) < 1) {
                throw new RoutePointsOutOfOrderException(i);
            }
            lastOffset = request.getRoutePoints()[i].getTimestamp();
        }
    }
}
