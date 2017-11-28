package Routing;


import GasStation.GasStation;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;

public class FixedGasStationTest {

    /**
     * GasStation builder
     * @param lat
     * @param lon
     * @param price
     * @return
     */
    public GasStation buildGasStation(float lat, float lon, float price) {
        GasStation station = new GasStation();
        station.lat = lat;
        station.lon = lon;
        station.cost = price;
        return station;
    }


    @Test
    /*
      Test the distance calculation
     */
    public void testDistance() {
        GasStation test1_1 = buildGasStation(30, 30, 0);
        GasStation test1_2 = buildGasStation(30, 30, 0);
        Assert.assertEquals(FixedGasStation.distanceGasStation(test1_1, test1_2), 0, 0);
        GasStation test2_1 = buildGasStation(0, 0, 0);
        GasStation test2_2 = buildGasStation(0, 1, 0);
        Assert.assertEquals(FixedGasStation.distanceGasStation(test2_1, test2_2), FixedGasStation.EARTHRADIUS, 1);
    }

    @Test
    /*
      Test the distance by range calculation
     */
    public void testDistanceByRange() {
        LinkedList<GasStation> testRoute = new LinkedList<GasStation>();
        testRoute.add(buildGasStation(0, 0, 0));
        testRoute.add(buildGasStation(0, 1, 0));
        testRoute.add(buildGasStation(0, 2, 0));
        Assert.assertEquals(FixedGasStation.distanceByRange(testRoute, 0, 2), FixedGasStation.EARTHRADIUS * 2, 0);
    }

    @Test
    /*
      Test if we get the correct succeeding GasStation
     */
    public void testGetSuccessor() {
        LinkedList<GasStation> testRoute = new LinkedList<GasStation>();
        testRoute.add(buildGasStation(0, 0, 3));
        Assert.assertEquals(FixedGasStation.getSuccessor(testRoute, 0, 50), -1); //no successor so -1
        testRoute.add(buildGasStation(0, 1, 2));
        testRoute.add(buildGasStation(0, 2, 1));
        Assert.assertEquals(FixedGasStation.getSuccessor(testRoute, 0, 50), 1); //next in list is cheaper so 1

    }


}