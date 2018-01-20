package com.github.robinbaumann.informaticup2018.routing.impl;

import com.github.robinbaumann.informaticup2018.model.GasStation;
import com.github.robinbaumann.informaticup2018.model.GasStop;
import com.github.robinbaumann.informaticup2018.model.GasStrategy;
import com.github.robinbaumann.informaticup2018.routing.api.IPricePredictionService;
import com.github.robinbaumann.informaticup2018.routing.api.RoutingStrategy;

import java.util.List;

public class NaiveStrategy extends RoutingStrategy {


    public NaiveStrategy(IPricePredictionService predictionService) {
        super(predictionService);
    }

    @Override
    public GasStrategy calculateRoute(List<GasStop> route, double capacity, double reserve) {
        int startIndex = 0;
        GasStop firstStop = route.get(0);
        for (GasStop gasStop : route) {
            gasStop.setPrice(pricePredictionService.getPrice(
                    gasStop.getStation(),
                    gasStop.getTimestamp(),
                    firstStop.getTimestamp()));
        }
        List<GasStation> stations = mapStations(route);
        for (int i = 0; i < stations.size(); i++) {
            if (i == 0) {
                route.get(i).setAmount(capacity - reserve);
            } else {
                double amount = distanceGasStation(stations.get(i), stations.get(i - 1)) * LITREPERKM;
                route.get(i).setAmount(capacity - amount);
            }
        }
        return new GasStrategy(route);
    }
}
