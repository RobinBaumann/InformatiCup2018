package com.github.robinbaumann.informaticup2018.webservice;

import com.github.robinbaumann.informaticup2018.database.Repository;
import com.github.robinbaumann.informaticup2018.model.*;
import com.github.robinbaumann.informaticup2018.routing.PricePredictionService;
import com.github.robinbaumann.informaticup2018.routing.SimpleRoutingService;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import spark.Request;
import spark.Response;

public class StationSparkProxy {
    private final static Gson GSON = Converters.registerOffsetDateTime(new GsonBuilder()).create();
    private final SimpleRoutingService simpleRoutingService;
    private final PricePredictionService pricePredictionService;
    private Repository repository;

    public StationSparkProxy(SimpleRoutingService simpleRoutingService,
                             PricePredictionService pricePredictionService,
                             Repository repository) {
        this.simpleRoutingService = simpleRoutingService;
        this.pricePredictionService = pricePredictionService;
        this.repository = repository;
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
            return GSON.toJson(repository.getStationById(parsedId));
        } catch (StationNotFoundException e) {
            return GSON.toJson(ProblemResponse.stationNotFound(e));
        }
    }

    /**
     * Create Route and solve Fixed Path Station Problem
     * @param request
     * @param response
     * @return
     */
    public String getStationsByRoute(Request request, Response response) {
        RouteRequest r;
        try {
            String body = request.body();
            r = GSON.fromJson(body, RouteRequest.class);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            return GSON.toJson(simpleRoutingService.route(r));
        } catch (EmptyRouteException e) {
            return GSON.toJson(ProblemResponse.emptyRoute());
        } catch (RoutePointsOutOfOrderException e) {
            return GSON.toJson(ProblemResponse.routePointsOutOfOrder(e));
        } catch (CapacityException e) {
            return GSON.toJson(ProblemResponse.capacityException(e));
        }
    }

    public String getPricePredictions(Request request, Response response) {
        PricePredictionRequests r;
        try {
            String body = request.body();
            r = GSON.fromJson(body, PricePredictionRequests.class);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e);
        }
        return GSON.toJson(pricePredictionService.predict(r));
    }
}
