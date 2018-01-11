import Routing.FixedGasStation;
import Routing.PricePredictionService;
import Routing.SimpleRoutingService;
import WebService.Router;
import WebService.StationSparkProxy;
import spark.Spark;


class Main {
    public static void main(String[] args) {
        Spark.staticFiles.location("/public");
        StationSparkProxy stationSparkProxy = new StationSparkProxy(
                new SimpleRoutingService(new FixedGasStation(new PricePredictionService())));
        Router router = new Router(stationSparkProxy);
        router.setupRouter();
    }
}
