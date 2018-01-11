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
    private PricePredictionService pricePredictionService;

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
        int i = 0;
        //TODO check if predictAll is better perf wise
        for (GasStop gasStop: route) {
            gasStop.setPrice(pricePredictionService.getPrice(gasStop.getStation(), gasStop.getTimestamp()));
        }
        while (i < route.size() - 1) {
            int next = getSuccessor(route, i, capacity);
            if (next == -1)
                next = route.size() - 1;
            if (distanceByRange(mapStations(route), i, next) <= U(full)) {
                full -= (distanceByRange(mapStations(route), i, next) * LITREPERKM);
                //TODO remove souts
                System.out.println("reaching from " + i + " to " + next + " distance from " + distanceByRange(mapStations(route), i, next) + " km ");
            } else {
                double fillingAmount = Math.abs(full - (distanceByRange(mapStations(route), i, next) * LITREPERKM));
                full = 0;
                System.out.println("filling in " + fillingAmount + " liters to reach " + next +
                        " from " + i + " for " + route.get(i).getPrice() * distanceByRange(mapStations(route), i, next) * LITREPERKM +
                        "€ for distance " + distanceByRange(mapStations(route), i, next) + " km ");
                route.get(i).setAmount(fillingAmount);
            }
            i = next;
        }
        return new GasStrategy(route);

    }


    /**
     * Destination from two Geo-points
     *
     * @param lat     latitude source
     * @param lon     longitude source
     * @param destLat latitude destination
     * @param destLon latitude destination
     * @return
     */
    private static double getDistance(double lat, double lon, double destLat, double destLon) {
        return EARTHRADIUS * Math.acos(Math.sin(lat) * Math.sin(destLat) + Math.cos(lat) * Math.cos(destLat) * Math.cos(destLon - lon));
    }


    /**
     * Calculate liter you can drive given a capacity
     *
     * @param capacity capacity of tank
     * @return
     */
    private static double U(double capacity) {
        return capacity / LITREPERKM;
    }

    /**
     * get the optimal fitting gasstation succeeding a given one
     * Implying there is always at least one reachable Gas Station you can reach with a full tank
     *
     * @param route
     * @param i        predeccessors which succcessor is searched
     * @param capacity capacity of tank
     * @return
     */
    protected static int getSuccessor(List<GasStop> route, int i, double capacity) {
        List<GasStop> prio = new ArrayList<>();
        for (int k = i + 1; k < route.size(); k++) {
            if (route.get(k).getPrice() <= route.get(i).getPrice()
                    && distanceByRange(mapStations(route), k, i)
                    < U(capacity)) {
                prio.add(route.get(k));
            }
        }
        if (prio.size() <= 0)
            return -1;
        prio.sort(new GasStopComparator(route.get(route.size() - 1)));
        return route.indexOf(prio.get(0));
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
        for (int k = i; k < j; k++) {
            sum += distanceGasStation(route.get(k), route.get(k + 1));
        }
        return sum;
    }

    private static List<GasStation> mapStations(List<GasStop> stops) {
        return stops.stream().map(GasStop::getStation).collect(Collectors.toList());
    }
}
