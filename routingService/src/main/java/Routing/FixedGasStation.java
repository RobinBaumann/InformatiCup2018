package Routing;

import GasStation.GasStation;

import java.util.*;

/**
 * implemented http://www.cs.umd.edu/projects/gas/gas-station.pdf Appendix B
 */
public class FixedGasStation {
    public static final Double EARTHRADIUS = 6378.388;
    //5.6 litre per 100km, german average 2016
    public static final Double LITREPERKM = 0.056;

    /**
     * Destination from two Geo-points
     *
     * @param lat
     * @param lon
     * @param destLat
     * @param destLon
     * @return
     */
    public static Double getDistance(Double lat, Double lon, Double destLat, Double destLon) {
        return EARTHRADIUS * Math.acos(Math.sin(lat) * Math.sin(destLat) + Math.cos(lat) * Math.cos(destLat) * Math.cos(destLon - lon));
    }

    /**
     * Distance from two Gas Stations
     *
     * @param x
     * @param y
     * @return
     */
    public static double distanceGasStation(GasStation x, GasStation y) {
        return getDistance(x.lat, x.lon, y.lat, y.lon);
    }

    /**
     * Calculate where we fill the tank
     *
     * @param route
     * @param capacity
     * @param full
     */
    public static void calculateRoute(LinkedList<GasStation> route, double capacity, double full) {
        int i = 0;
        while (i < route.size() - 1) {
            int next = getSuccessor(route, i, capacity);
            if (next == -1)
                next = route.size() - 1;
            if (distanceByRange(route, i, next) <= U(full)) {
                full -= distanceByRange(route, i, next) * LITREPERKM;
                System.out.println("reaching from " + i + " to " + next + " distance from " + distanceByRange(route, i, next) + " km ");
            } else {
                double fillingAmount = (distanceByRange(route, i, next) * LITREPERKM) - full;
                System.out.println("filling in " + fillingAmount + " liters to reach " + next + " from " + i + " for " + route.get(i).cost * distanceByRange(route, i, next) * LITREPERKM + "€");

            }
            i = next;
        }
    }

    /**
     * Calculate liter you can drive given a capacity
     *
     * @param capacity
     * @return
     */
    public static double U(double capacity) {
        return capacity / LITREPERKM;
    }

    /**
     * get the optimal fitting gasstation succeeding a given one
     * Implying there is always at least one reachable Gas Station you can reach with a full tank
     *
     * @param route
     * @param i
     * @param capacity
     * @return
     */
    public static int getSuccessor(LinkedList<GasStation> route, int i, double capacity) {
        ArrayList<GasStation> prio = new ArrayList<GasStation>();
        for (int k = i + 1; k < route.size(); k++) {
            if (route.get(k).cost <= route.get(i).cost && distanceByRange(route, k, i) < U(capacity)) {
                prio.add(route.get(k));
            }
        }
        if (prio.size() <= 0)
            return -1;
        prio.sort(new GasStationComparator());
        return route.indexOf(prio.get(0));
    }

    /**
     * Distance from two Gas Station given a route and indices
     *
     * @param route
     * @param i
     * @param j
     * @return
     */
    public static double distanceByRange(LinkedList<GasStation> route, int i, int j) {
        double sum = 0;
        for (int k = i; k < j; k++) {
            sum += distanceGasStation(route.get(k), route.get(k + 1));
        }
        return sum;
    }

    public static void main(String[] args) {
        LinkedList<GasStation> route = new LinkedList<GasStation>();
        for (int i = 0; i <= 10; i++) {
            GasStation g = new GasStation();
            Random rand = new Random();
            g.lat = (double) i / 200;
            g.lon = (double) i / 200;
            g.station_name = "" + i;
            g.id = i;
            g.cost = 10 - i;
            route.add(g);
        }
        double capacity = 50;
        long full = 5;
        int i = 0;
        calculateRoute(route, capacity, full);

    }
}
