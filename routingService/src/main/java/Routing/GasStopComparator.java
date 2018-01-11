package Routing;

import Model.GasStop;

import java.util.Comparator;

class GasStopComparator implements Comparator<GasStop> {
    private final GasStop last; //Destination


    public GasStopComparator(GasStop last) {
        this.last = last;
    }

    @Override
    public int compare(GasStop x, GasStop y) {
        if (x.getPrice() < y.getPrice() || x.getPrice() == y.getPrice()
                && FixedGasStation.distanceGasStation(x.getStation(), last.getStation())
                < FixedGasStation.distanceGasStation(y.getStation(), last.getStation()))
            return 1;
        else if (x.getPrice() > y.getPrice() || x.getPrice() == y.getPrice()
                && FixedGasStation.distanceGasStation(x.getStation(), last.getStation())
                > FixedGasStation.distanceGasStation(y.getStation(), last.getStation()))
            return -1;
        else
            return 0;
    }
}
