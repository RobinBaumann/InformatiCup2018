package Routing;

import GasStation.GasStation;

import java.util.Comparator;

class GasStationComparator implements Comparator<GasStation> {
    private final GasStation last; //Destination


    public GasStationComparator(GasStation last) {
        this.last = last;
    }

    @Override
    public int compare(GasStation x, GasStation y) {
        if (x.cost < y.cost || x.cost == y.cost && FixedGasStation.distanceGasStation(x, last) < FixedGasStation.distanceGasStation(y, last))
            return 1;
        else if (x.cost > y.cost || x.cost == y.cost && FixedGasStation.distanceGasStation(x, last) > FixedGasStation.distanceGasStation(y, last))
            return -1;
        else
            return 0;
    }
}
