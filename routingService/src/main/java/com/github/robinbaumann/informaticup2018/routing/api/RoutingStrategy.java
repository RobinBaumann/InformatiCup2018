package com.github.robinbaumann.informaticup2018.routing.api;

import com.github.robinbaumann.informaticup2018.model.GasStation;
import com.github.robinbaumann.informaticup2018.model.GasStop;

import java.util.List;
import java.util.stream.Collectors;

public abstract class RoutingStrategy implements IRoutingStrategy{

    protected IPricePredictionService pricePredictionService;
    protected static final double EARTHRADIUS = 6378.388;
    //5.6 litre per 100km, german average 2016
    protected static final double LITREPERKM = 0.056;

    /**
     * @param pricePredictionService
     */
    protected RoutingStrategy(IPricePredictionService pricePredictionService) {
        this.pricePredictionService = pricePredictionService;
    }

    /**
     * Distance from two Gas Stations
     *
     * @param x gasStation x
     * @param y gasStation y
     * @return
     */
    protected static double distanceGasStation(GasStation x, GasStation y) {
        return getDistance(x.getLat(), x.getLon(), y.getLat(), y.getLon());
    }


    /**
     * Calculate kilometre you can drive given a capacity
     *
     * @param capacity capacity of tank
     * @return
     */
    protected static double capacityToKilometres(double capacity) {
        return capacity / LITREPERKM;
    }


    /**
     * Distance from two Gas Station given a route and indices
     *
     * @param route
     * @param from  start index
     * @param to    destination index
     * @return
     */
    protected static double distanceByRange(List<GasStation> route, int from, int to) {
        double sum = 0;
        for (int k = from; k < to; k++)
            sum += distanceGasStation(route.get(k), route.get(k + 1));
        return sum;
    }

    protected static List<GasStation> mapStations(List<GasStop> stops) {
        return stops.stream().map(GasStop::getStation).collect(Collectors.toList());
    }

    /**
     * @param degrees
     * @return
     */
    protected static double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }


    /**
     * Calculate distance between two geo points
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    protected static double getDistance(double lat1, double lon1, double lat2, double lon2) {

        double dLat = degreesToRadians(lat2 - lat1);
        double dLon = degreesToRadians(lon2 - lon1);

        lat1 = degreesToRadians(lat1);
        lat2 = degreesToRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTHRADIUS * c;
    }
}
