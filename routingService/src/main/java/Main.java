    import Database.Repository;
import Model.PricePrediction;
import Routing.FixedGasStation;
import Routing.PricePredictionService;
import Routing.SimpleRoutingService;
import WebService.Router;
import WebService.StationSparkProxy;
import spark.Spark;


class Main {
    public static void main(String[] args) {
        Spark.staticFiles.location("/public");
        // poor mans DI
        Repository repository = new Repository();
        PricePredictionService pricePredictionService = new PricePredictionService(repository);
        StationSparkProxy stationSparkProxy =
                new StationSparkProxy(
                        new SimpleRoutingService(new FixedGasStation(pricePredictionService), repository),
                        pricePredictionService,
                        repository
                );
        Router router = new Router(stationSparkProxy);
        router.setupRouter();
    }
}
