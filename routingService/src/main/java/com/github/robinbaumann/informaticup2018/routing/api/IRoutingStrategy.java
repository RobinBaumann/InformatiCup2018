package com.github.robinbaumann.informaticup2018.routing.api;

import com.github.robinbaumann.informaticup2018.model.GasStop;
import com.github.robinbaumann.informaticup2018.model.GasStrategy;

import java.util.List;

public interface IRoutingStrategy {
    GasStrategy calculateRoute(List<GasStop> route, double capacity, double reserve);
}
