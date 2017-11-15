package WebService;

import static spark.Spark.*;

import java.util.logging.Logger;

public class Router {
    private final static Logger LOGGER = Logger.getLogger(Router.class.getName());

    StationSparkProxy stationSparkProxy = null;

    public Router(StationSparkProxy stationSparkProxy) {
        this.stationSparkProxy = stationSparkProxy;
    }

    public void setupRouter() {
        path("/api", () -> {
            before("/*", (q, a) -> LOGGER.info("Received api call"));
            path("/gasStation", () -> {
                get("/info/:id", (q, a) -> this.stationSparkProxy.get(q, a));
            });

        });
    }
}