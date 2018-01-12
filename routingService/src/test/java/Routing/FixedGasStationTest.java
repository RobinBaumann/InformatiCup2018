package Routing;


import Model.GasStation;
import Model.GasStop;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;

public class FixedGasStationTest {

    /**
     * GasStation builder
     * @param lat
     * @param lon
     * @return
     */
    public GasStation buildGasStation(float lat, float lon) {
        GasStation station = new GasStation();
        station.lat = lat;
        station.lon = lon;
        return station;
    }




    @Test
    /*
      Test if we get the correct succeeding GasStation
     */
    public void testGetSuccessor() {
        LinkedList<GasStop> testRoute = new LinkedList<>();
        testRoute.add(new GasStop(buildGasStation(0, 0),3));
        Assert.assertEquals(FixedGasStation.getSuccessor(testRoute, 0, 50), -1); //no successor so -1
        testRoute.add(new GasStop(buildGasStation(0, 1), 2));
        testRoute.add(new GasStop(buildGasStation(0, 2), 1));
        Assert.assertEquals(FixedGasStation.getSuccessor(testRoute, 0, 50), 1); //next in list is cheaper so 1
    }
}