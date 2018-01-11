package WebService;

import Database.ConnectionFactory;
import GasStation.AbstractStation;
import GasStation.GasStation;
import Model.*;
import Routing.SimpleRoutingService;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.noelherrick.jell.Jell;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StationSparkProxy {
    private final static Logger LOGGER = Logger.getLogger(StationSparkProxy.class.getName());
    private final static Gson GSON = Converters.registerOffsetDateTime(new GsonBuilder()).create();
    private SimpleRoutingService simpleRoutingService;

    public StationSparkProxy(SimpleRoutingService simpleRoutingService) {
        this.simpleRoutingService = simpleRoutingService;
    }

    // Query function
    private final Function<String, Collection<GasStation>> queryStationMethod = (Function<String, Collection<GasStation>>) s -> {
        Collection<GasStation> gasStations = null;
        try (Connection con = ConnectionFactory.getConnection()){
            Jell jell = new Jell(con);
            gasStations = jell.query(s, GasStation.class);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL Exception " + e.toString());
        } catch (InstantiationException e) {
            LOGGER.log(Level.SEVERE, "Instantiation Exception " + e.toString());
        } catch (IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, "Illegal Access Exception " + e.toString());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception " + e.toString());
        }
        return gasStations;
    };


    /**
     * Retrieve Station by ID
     * @param request
     * @param response
     * @return
     */
    public Collection<GasStation> getStationByID(Request request, Response response) {
        return AbstractStation.getByID(Integer.parseInt(request.params(":id")), queryStationMethod);
    }

    /**
     * TODO:
     * Create Route and solve Fixed Path Station Problem
     * @param request
     * @param response
     * @return
     */
    public String getStationsByRoute(Request request, Response response) {
        RouteRequest r;
        try {
            r = GSON.fromJson(request.body(), RouteRequest.class);
        } catch (JsonSyntaxException e) {
            LOGGER.log(Level.SEVERE, "JSON Syntax Error " + e.toString());
            throw new RuntimeException(e);
        }
        try {
            return GSON.toJson(simpleRoutingService.route(r));
            //TODO translate to Problems
        } catch (EmptyRouteException e) {
            e.printStackTrace();
        } catch (RoutePointsOutOfOrderException e) {
            e.printStackTrace();
        } catch (CapacityException e) {
            e.printStackTrace();
        }
        LOGGER.severe("Error in getStationsByRoute, this point should never be reached");
        throw new RuntimeException();
    }
}
