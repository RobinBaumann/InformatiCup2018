package Routing;

import GasStation.GasStation;

import java.util.*;

/**
 * implemented http://www.cs.umd.edu/projects/gas/gas-station.pdf Appendix B
 */
class FixedGasStation {
    private static final double EARTHRADIUS = 6378.388;
    //5.6 litre per 100km, german average 2016
    private static final double LITREPERKM = 0.056;

    /**
     * Destination from two Geo-points
     *
     * @param lat
     * @param lon
     * @param destLat
     * @param destLon
     * @return
     */
    private static Double getDistance(double lat, double lon, double destLat, double destLon) {
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
    private static void calculateRoute(LinkedList<GasStation> route, double capacity, double full) {
        int i = 0;
        while (i < route.size() - 1) {
            int next = getSuccessor(route, i, capacity);
            if (next == -1)
                next = route.size() - 1;
            if (distanceByRange(route, i, next) <= U(full)) {
                full -= (distanceByRange(route, i, next) * LITREPERKM);
                System.out.println("reaching from " + i + " to " + next + " distance from " + distanceByRange(route, i, next) + " km ");
            } else {
                double fillingAmount = Math.abs(full - (distanceByRange(route, i, next) * LITREPERKM));
                full = 0;
                System.out.println("filling in " + fillingAmount + " liters to reach " + next + " from " + i + " for " + route.get(i).cost * distanceByRange(route, i, next) * LITREPERKM + "€ for distance " + distanceByRange(route, i, next) + " km ");

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
    private static double U(double capacity) {
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
    private static int getSuccessor(LinkedList<GasStation> route, int i, double capacity) {
        ArrayList<GasStation> prio = new ArrayList<GasStation>();
        for (int k = i + 1; k < route.size(); k++) {
            if (route.get(k).cost <= route.get(i).cost && distanceByRange(route, k, i) < U(capacity)) {
                prio.add(route.get(k));
            }
        }
        if (prio.size() <= 0)
            return -1;
        prio.sort(new GasStationComparator(route.getLast()));
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
    private static double distanceByRange(LinkedList<GasStation> route, int i, int j) {
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
            g.lat = (double) i / 180;
            g.lon = (double) i / 180;
            g.station_name = "" + i;
            g.id = i;
            g.cost = 10 - i;
            route.add(g);
        }
        double capacity = 50;
        double full = 3;
        int i = 0;
        calculateRoute(route, capacity, full);

    }
}
