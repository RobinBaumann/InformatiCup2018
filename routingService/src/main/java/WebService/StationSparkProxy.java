package WebService;

import Database.Repository;
import Model.*;
import Routing.SimpleRoutingService;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import spark.Request;
import spark.Response;

import java.util.logging.Logger;

public class StationSparkProxy {
    private final static Logger LOGGER = Logger.getLogger(StationSparkProxy.class.getName());
    private final static Gson GSON = Converters.registerOffsetDateTime(new GsonBuilder()).create();
    private SimpleRoutingService simpleRoutingService;

    public StationSparkProxy(SimpleRoutingService simpleRoutingService) {
        this.simpleRoutingService = simpleRoutingService;
    }

    /**
     * Retrieve Station by ID
     * @param request
     * @param response
     * @return
     */
    public String getStationByID(Request request, Response response) {
        String id = request.params(":id");
        int parsedId;
        try {
            parsedId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return GSON.toJson(ProblemResponse.canNotParseParameter("GasStationId has to be an Integer"));
        }
        try {
            return GSON.toJson(Repository.getStationById(parsedId));
        } catch (StationNotFoundException e) {
            return GSON.toJson(ProblemResponse.stationNotFound(e));
        }
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
        throw new RuntimeException("Error in getStationsByRoute, this point should never be reached.");
    }
}
