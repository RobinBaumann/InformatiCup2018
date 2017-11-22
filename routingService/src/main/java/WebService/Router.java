package WebService;

import static spark.Spark.*;

import java.util.logging.Logger;

public class Router {
    private final static Logger LOGGER = Logger.getLogger(Router.class.getName());

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
}