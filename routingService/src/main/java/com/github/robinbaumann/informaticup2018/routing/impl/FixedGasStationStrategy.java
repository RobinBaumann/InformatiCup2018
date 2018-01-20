package com.github.robinbaumann.informaticup2018.routing.impl;

import com.github.robinbaumann.informaticup2018.model.GasStation;
import com.github.robinbaumann.informaticup2018.model.GasStop;
import com.github.robinbaumann.informaticup2018.model.GasStrategy;
import com.github.robinbaumann.informaticup2018.routing.api.IPricePredictionService;
import com.github.robinbaumann.informaticup2018.routing.api.IRoutingStrategy;

import java.util.*;
import java.util.stream.Collectors;

/**
 * implemented http://www.cs.umd.edu/projects/gas/gas-station.pdf Appendix B
 */
public class FixedGasStationStrategy implements IRoutingStrategy {
    protected static final double EARTHRADIUS = 6378.388;
    //5.6 litre per 100km, german average 2016
    private static final double LITREPERKM = 0.056;
    public static final int NOSUCCESSOR = -1;

    private IPricePredictionService pricePredictionService;

    /**
     * @param pricePredictionService
     */
    public FixedGasStationStrategy(IPricePredictionService pricePredictionService) {
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
     * Calculate where we fill the tank
     *
     * @param route
     * @param capacity capacity of tank
     * @param reserve  start fuel of tank
     */
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

        while (startIndex < route.size() - 1) {
            int successorIndex = getSuccessor(new LinkedList<>(route), startIndex, capacity);
            if (successorIndex == NOSUCCESSOR)
                successorIndex = route.size() - 1;
            if (distanceByRange(mapStations(route), startIndex, successorIndex) <= capacityToKilometres(reserve)) {
                reserve -= (distanceByRange(mapStations(route), startIndex, successorIndex) * LITREPERKM);
            } else {
                double fillingAmount = (distanceByRange(mapStations(route), startIndex, successorIndex) * LITREPERKM) - reserve;
                reserve = 0;
                route.get(startIndex).setAmount(fillingAmount);
            }
            startIndex = successorIndex;
        }
        return new GasStrategy(route);

    }


    /**
     * @param degrees
     * @return
     */
    private static double degreesToRadians(double degrees) {
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
    private static double getDistance(double lat1, double lon1, double lat2, double lon2) {

        double dLat = degreesToRadians(lat2 - lat1);
        double dLon = degreesToRadians(lon2 - lon1);

        lat1 = degreesToRadians(lat1);
        lat2 = degreesToRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTHRADIUS * c;
    }

    /**
     * Calculate kilometre you can drive given a capacity
     *
     * @param capacity capacity of tank
     * @return
     */
    private static double capacityToKilometres(double capacity) {
        return capacity / LITREPERKM;
    }

    /**
     * get the optimal fitting gasstation succeeding a given one
     * Implying there is always at least one reachable Gas Station you can reach with a full tank
     *
     * @param route
     * @param startIndex predeccessors which succcessor is searched
     * @param capacity   capacity of tank
     * @return
     */
    protected static int getSuccessor(LinkedList<GasStop> route, int startIndex, double capacity) {
        LinkedList<GasStop> prio = new LinkedList<>();
        for (int successorIndex = startIndex + 1; successorIndex < route.size() - 1; successorIndex++) {
            if (distanceByRange(mapStations(route), startIndex, successorIndex) * LITREPERKM < (capacity))
                prio.add(route.get(successorIndex));
        }
        if (prio.size() <= 0)
            return NOSUCCESSOR;
        prio.sort(new GasStopComparator(route.getLast()));
        return route.indexOf(prio.getFirst());
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

    private static List<GasStation> mapStations(List<GasStop> stops) {
        return stops.stream().map(GasStop::getStation).collect(Collectors.toList());
    }


}
