package Routing;

import GasStation.GasStation;

import java.util.Comparator;

public class GasStationComparator implements Comparator<GasStation> {


    @Override
    public int compare(GasStation x, GasStation y) {
        if (x.cost < y.cost)
            return 1;
        else if (x.cost > y.cost)
            return -1;
        else
            return 0;
    }
}
