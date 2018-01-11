import Routing.SimpleRoutingService;
import WebService.Router;
import WebService.StationSparkProxy;
import spark.Spark;

import java.util.logging.Logger;

class Main {
    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Spark.staticFiles.location("/public");
        StationSparkProxy stationSparkProxy = new StationSparkProxy(
                new SimpleRoutingService());
        Router router = new Router(stationSparkProxy);
        router.setupRouter();
    }
}
