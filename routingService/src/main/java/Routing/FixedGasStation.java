package Routing;

import GasStation.GasStation;

import java.time.LocalTime;
import java.util.*;

public class FixedGasStation {
    public static final Double EARTHRADIUS = 6378.388;
    //5.6 litre per 100km, german average 2016
    public static final Double LITREPERKM = 0.056;

    /**
     * Destination from two Geo-points
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
     * @param x
     * @param y
     * @return
     */
    public static double distanceGasStation(GasStation x, GasStation y) {
        return getDistance(x.lat, x.lon, y.lat, y.lon);
    }

    public static void calculateRoute(LinkedList<GasStation> route, LocalTime time, Double capacity, Double fuelState) {

    }

    /**
     * Calculate liter you can drive given a capacity
     * @param capacity
     * @return
     */
    public static double U(double capacity) {
        return capacity / LITREPERKM;
    }

    /**
     * get the distance in liters
     * @param x
     * @param y
     * @return
     */
    public static Double d(GasStation x, GasStation y) {
        return distanceGasStation(x, y) * LITREPERKM;
    }

    /**
     * get the optimal fitting gasstation succeeding a given one
     * @param route
     * @param full
     * @param i
     * @param capacity
     * @return
     */
    public static int getSuccessor(LinkedList<GasStation> route, long full, int i, double capacity) {
        ArrayList<GasStation> prio = new ArrayList<GasStation>();
        for (int k = i + 1; k < route.size(); k++) {
            if (route.get(k).cost <= route.get(i).cost && distanceGasStation(route.get(k), route.get(i)) < U(capacity)) {
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
            g.cost =  rand.nextDouble()/100 + 1;
            route.add(g);
        }
        double capacity = 50;
        long full = 0;
        int i = 0;
        while (i < route.size() - 1) {
            int next = getSuccessor(route, full, i, capacity);
            if (next == -1)
                next = route.size() - 1;
            if (distanceByRange(route, i, next) <= U(full)) {
                full -= distanceByRange(route, i, next) * LITREPERKM;
                System.out.println("reaching from " + i + " to " + next + " distance from " + distanceByRange(route, i, next) + " km ");
            } else {
                System.out.println("insuficient fuel filling in " + distanceByRange(route, i, next) * LITREPERKM + " liters to reach " + next + " from " + i + " for " + route.get(next).cost * distanceByRange(route, i, next)*LITREPERKM + "€");

            }
            i = next;
        }

    }
}
