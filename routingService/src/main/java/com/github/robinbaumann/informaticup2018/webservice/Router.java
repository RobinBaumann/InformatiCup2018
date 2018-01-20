package com.github.robinbaumann.informaticup2018.webservice;

import com.github.robinbaumann.informaticup2018.model.ProblemResponse;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static spark.Spark.*;

import java.text.MessageFormat;
import java.util.logging.Logger;

public class Router {
    private final static Logger LOGGER = Logger.getLogger(Router.class.getName());
    private final static Gson GSON = Converters.registerOffsetDateTime(new GsonBuilder()).create();
    public final static String API_PREFIX = "/api";
    public final static String GASSTRAT_ROUTE = "/simpleRoute";
    public final static String PRICEPRED_ROUTE = "/pricePredictions";

    private final ApiHandler apiHandler;

    public Router(ApiHandler apiHandler) {
        this.apiHandler = apiHandler;
    }

    //localhost:4567/api/gasStation/info/1 retrieves the first gasStation
    public void setupRouter() {
        enableCORS(
                "http://localhost:8080",
                "GET, PUT, POST, DELETE, OPTIONS",
                "DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range");
        exception(RuntimeException.class, (exception, request, response) -> {
            LOGGER.warning(MessageFormat.format("Unhandled exception: {0}", exception));
            response.status(500);
            response.body(GSON.toJson(ProblemResponse.internalError()));
        });
        path(API_PREFIX, () -> {
            before("/*", (q, a) -> {
                LOGGER.info("Received api call");
            });
            post(GASSTRAT_ROUTE, this.apiHandler::getStationsByRoute);
            post(PRICEPRED_ROUTE, this.apiHandler::getPricePredictions);
            path("/gasStation", () ->
                    get("/:id", this.apiHandler::getStationByID)
            );
        });
    }

    private static void enableCORS(String origin, String methods, String headers) {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Request-Method", accessControlRequestMethod);
            }

            return "OK";
        });
        before(((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
            response.type("application/json");
        }));
    }
}