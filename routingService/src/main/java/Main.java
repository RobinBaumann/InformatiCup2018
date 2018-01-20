import com.github.robinbaumann.informaticup2018.database.Repository;
import com.github.robinbaumann.informaticup2018.routing.FixedGasStation;
import com.github.robinbaumann.informaticup2018.routing.PricePredictionService;
import com.github.robinbaumann.informaticup2018.routing.SimpleRoutingService;
import com.github.robinbaumann.informaticup2018.webservice.Router;
import com.github.robinbaumann.informaticup2018.webservice.StationSparkProxy;
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
