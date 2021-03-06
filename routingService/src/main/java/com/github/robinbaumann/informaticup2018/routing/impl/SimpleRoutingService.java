package com.github.robinbaumann.informaticup2018.routing.impl;

import com.github.robinbaumann.informaticup2018.database.api.IRepository;
import com.github.robinbaumann.informaticup2018.model.*;
import com.github.robinbaumann.informaticup2018.routing.api.IRoutingService;
import com.github.robinbaumann.informaticup2018.routing.api.IRoutingStrategy;
import com.github.robinbaumann.informaticup2018.validation.RouteRequestValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleRoutingService implements IRoutingService {
    private final static double STARTING_AMOUNT = 0;
    private IRoutingStrategy fixedGasStation;
    private IRepository repository;

    public SimpleRoutingService(
            IRoutingStrategy fixedGasStation,
            IRepository repository
    ) {
        this.fixedGasStation = fixedGasStation;
        this.repository = repository;
    }

    @Override
    public GasStrategy route(RouteRequest request) throws EmptyRouteException, RoutePointsOutOfOrderException, CapacityException, StationNotFoundException, StationNotReachableException, StationWithoutPricesException {
        RouteRequestValidator.validate(request);
        List<Integer> requestedIds =
                request.getRoutePoints().stream().map(RoutePoint::getStationId).collect(Collectors.toList());
        Map<Integer, GasStation> gasStations = repository.getStationsByIds(
                requestedIds)
                .stream().collect(Collectors.toMap(GasStation::getId, Function.identity()));
        HashSet<Integer> idSet = new HashSet<>(requestedIds);
        if (!(idSet.equals(gasStations.keySet()))) {
            idSet.removeAll(gasStations.keySet());
            throw new StationNotFoundException(idSet);
        }
        List<GasStop> gasStops = request.getRoutePoints().stream()
                .map(p -> new GasStop(p.getTimestamp(), gasStations.get(p.getStationId())))
                .collect(Collectors.toList());

        return fixedGasStation.calculateRoute(gasStops, request.getCapacity(), STARTING_AMOUNT);
    }

}
