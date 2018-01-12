package Routing;

import Model.GasStation;
import Model.GasStop;
import Model.GasStrategy;

import java.util.*;
import java.util.stream.Collectors;

/**
 * implemented http://www.cs.umd.edu/projects/gas/gas-station.pdf Appendix B
 */
public class FixedGasStation {
    protected static final double EARTHRADIUS = 6378.388;
    //5.6 litre per 100km, german average 2016
    private static final double LITREPERKM = 0.056;
    public static final int NOSUCCESSOR = -1;

    private PricePredictionService pricePredictionService;

    /**
     * @param pricePredictionService
     */
    public FixedGasStation(PricePredictionService pricePredictionService) {
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
        return getDistance(x.lat, x.lon, y.lat, y.lon);
    }


    /**
     * Calculate where we fill the tank
     *
     * @param route
     * @param capacity capacity of tank
     * @param full     start fuel of tank
     */
    public GasStrategy calculateRoute(List<GasStop> route, double capacity, double full) {
        int startIndex = 0;
        //TODO check if predictAll is better perf wise
        for (GasStop gasStop : route) {
            gasStop.setPrice(pricePredictionService.getPrice(gasStop.getStation(), gasStop.getTimestamp()));
        }

        while (startIndex < route.size() - 1) {
            int successorIndex = getSuccessor(new LinkedList<>(route), startIndex, capacity);
            if (successorIndex == NOSUCCESSOR)
                successorIndex = route.size() - 1;
            if (distanceByRange(mapStations(route), startIndex, successorIndex) <= capacityToLitre(full)) {
                full -= (distanceByRange(mapStations(route), startIndex, successorIndex) * LITREPERKM);
            } else {
                double fillingAmount = Math.abs(full - (distanceByRange(mapStations(route), startIndex, successorIndex) * LITREPERKM));
                full = 0;
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
     * Calculate liter you can drive given a capacity
     *
     * @param capacity capacity of tank
     * @return
     */
    private static double capacityToLitre(double capacity) {
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
            if (route.get(successorIndex).getPrice() <= route.get(startIndex).getPrice()
                    && distanceByRange(mapStations(route), startIndex, successorIndex)
                    < capacityToLitre(capacity)) {
                prio.add(route.get(successorIndex));
            }
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
     * @param i     start index
     * @param j     destination index
     * @return
     */
    protected static double distanceByRange(List<GasStation> route, int i, int j) {
        double sum = 0;
        for (int k = i; k < j; k++)
            sum += distanceGasStation(route.get(k), route.get(k + 1));

        return sum;
    }

    private static List<GasStation> mapStations(List<GasStop> stops) {
        return stops.stream().map(GasStop::getStation).collect(Collectors.toList());
    }


}
