package com.github.robinbaumann.informaticup2018.validation;

import com.github.robinbaumann.informaticup2018.model.CapacityException;
import com.github.robinbaumann.informaticup2018.model.EmptyRouteException;
import com.github.robinbaumann.informaticup2018.model.RoutePointsOutOfOrderException;
import com.github.robinbaumann.informaticup2018.model.RouteRequest;

import java.time.OffsetDateTime;

public class RouteRequestValidator {
    public static void validate(RouteRequest request)
            throws CapacityException, EmptyRouteException, RoutePointsOutOfOrderException {
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
