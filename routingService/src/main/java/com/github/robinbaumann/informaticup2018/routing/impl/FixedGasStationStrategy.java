package com.github.robinbaumann.informaticup2018.routing.impl;

import com.github.robinbaumann.informaticup2018.model.GasStop;
import com.github.robinbaumann.informaticup2018.model.GasStrategy;
import com.github.robinbaumann.informaticup2018.model.StationNotReachableException;
import com.github.robinbaumann.informaticup2018.model.StationWithoutPricesException;
import com.github.robinbaumann.informaticup2018.routing.api.IPricePredictionService;

import java.util.LinkedList;
import java.util.List;

/**
 * implemented http://www.cs.umd.edu/projects/gas/gas-station.pdf Appendix B
 */
public class FixedGasStationStrategy extends RoutingStrategy {

    public static final int NOSUCCESSOR = -1;


    public FixedGasStationStrategy(IPricePredictionService pricePredictionService) {
        super(pricePredictionService);
    }


    /**
     * Calculate where we fill the tank
     *
     * @param route
     * @param capacity capacity of tank
     * @param reserve  start fuel of tank
     */
    @Override
    public GasStrategy calculateRoute(List<GasStop> route, double capacity, double reserve) throws StationNotReachableException, StationWithoutPricesException {
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
     * get the optimal fitting gasstation succeeding a given one
     * Implying there is always at least one reachable Gas Station you can reach with a full tank
     *
     * @param route
     * @param startIndex predeccessors which succcessor is searched
     * @param capacity   capacity of tank
     * @return
     */
    protected static int getSuccessor(LinkedList<GasStop> route, int startIndex, double capacity) throws StationNotReachableException {
        LinkedList<GasStop> prio = new LinkedList<>();
        int potentialNextStations = 0;
        for (int successorIndex = startIndex + 1; successorIndex < route.size() - 1; successorIndex++) {
            potentialNextStations++;
            if (distanceByRange(mapStations(route), startIndex, successorIndex) * LITREPERKM < (capacity))
                prio.add(route.get(successorIndex));
        }
        if (prio.size() <= 0 && potentialNextStations == 0) {
            return NOSUCCESSOR;
        } else if (prio.size() <= 0 && potentialNextStations > 0) {
            throw new StationNotReachableException();
        }
        prio.sort(new GasStopComparator(route.getLast()));
        return route.indexOf(prio.getFirst());
    }


}
