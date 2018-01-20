package com.github.robinbaumann.informaticup2018.routing.api;

import com.github.robinbaumann.informaticup2018.model.*;

public interface IRoutingService {
    GasStrategy route(RouteRequest request) throws EmptyRouteException, RoutePointsOutOfOrderException, CapacityException;
}
