package WebService;

import static spark.Spark.*;

import java.util.logging.Logger;

public class Router {
    private final static Logger LOGGER = Logger.getLogger(Router.class.getName());

    static {
        enableCORS(
                "http://localhost:8080",
                "GET, PUT, POST, DELETE, OPTIONS",
                "DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range");
    }

    private StationSparkProxy stationSparkProxy = null;

    public Router(StationSparkProxy stationSparkProxy) {
        this.stationSparkProxy = stationSparkProxy;
    }

    //localhost:4567/api/gasStation/info/1 retrieves the first gasStation
    public void setupRouter() {
        path("/api", () -> {
            before("/*", (q, a) -> LOGGER.info("Received api call"));
            path("/gasStation", () -> {
                get("/info/:id", (q, a) -> this.stationSparkProxy.getStationByID(q, a));
                post("/route", (q, a) -> this.stationSparkProxy.getStationsByRoute(q, a));
            });

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