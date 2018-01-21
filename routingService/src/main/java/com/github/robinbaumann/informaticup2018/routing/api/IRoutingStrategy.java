package com.github.robinbaumann.informaticup2018.routing.api;

import com.github.robinbaumann.informaticup2018.model.GasStop;
import com.github.robinbaumann.informaticup2018.model.GasStrategy;
import com.github.robinbaumann.informaticup2018.model.StationNotReachableException;
import com.github.robinbaumann.informaticup2018.model.StationWithoutPricesException;

import java.util.List;

public interface IRoutingStrategy {
    GasStrategy calculateRoute(List<GasStop> route, double capacity, double reserve) throws StationNotReachableException, StationWithoutPricesException;
}
