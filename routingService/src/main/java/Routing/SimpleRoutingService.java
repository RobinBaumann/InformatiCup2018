package Routing;

import Database.Repository;
import Model.GasStation;
import Model.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleRoutingService {
    private final static double STARTING_AMOUNT = 0;
    private FixedGasStation fixedGasStation;
    private Repository repository;

    public SimpleRoutingService(
            FixedGasStation fixedGasStation,
            Repository repository
    ){
        this.fixedGasStation = fixedGasStation;
        this.repository = repository;
    }

    public GasStrategy route(RouteRequest request) throws EmptyRouteException, RoutePointsOutOfOrderException, CapacityException {
        validate(request);
        Map<Integer, GasStation> gasStations = repository.getStationsByIds(
                request.getRoutePoints().stream().map(RoutePoint::getStationId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(GasStation::getId, Function.identity()));
        List<GasStop> gasStops = request.getRoutePoints().stream()
                .map(p -> new GasStop(p.getTimestamp(), gasStations.get(p.getStationId())))
                .collect(Collectors.toList());
        return fixedGasStation.calculateRoute(gasStops, request.getCapacity(), STARTING_AMOUNT);
    }

    private void validate(RouteRequest request) throws CapacityException, EmptyRouteException, RoutePointsOutOfOrderException {
        if (request.getCapacity() < 1) {
            throw new CapacityException(request.getCapacity());
        }
        if (request.getRoutePoints().size() < 1) {
            throw new EmptyRouteException();
        }
        OffsetDateTime lastOffset = request.getRoutePoints().get(0).getTimestamp();
        for (int i = 1; i < request.getRoutePoints().size(); i++) {
            if (request.getRoutePoints().get(i).getTimestamp().compareTo(lastOffset) < 1) {
                throw new RoutePointsOutOfOrderException(i);
            }
            lastOffset = request.getRoutePoints().get(i).getTimestamp();
        }
    }
}
