package com.github.robinbaumann.informaticup2018.routing;

import com.github.robinbaumann.informaticup2018.database.Repository;
import com.github.robinbaumann.informaticup2018.model.GasStation;
import com.github.robinbaumann.informaticup2018.model.*;
import com.github.robinbaumann.informaticup2018.validation.RouteRequestValidator;

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
        RouteRequestValidator.validate(request);
        Map<Integer, GasStation> gasStations = repository.getStationsByIds(
                request.getRoutePoints().stream().map(RoutePoint::getStationId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(GasStation::getId, Function.identity()));
        List<GasStop> gasStops = request.getRoutePoints().stream()
                .map(p -> new GasStop(p.getTimestamp(), gasStations.get(p.getStationId())))
                .collect(Collectors.toList());
        return fixedGasStation.calculateRoute(gasStops, request.getCapacity(), STARTING_AMOUNT);
    }

}
