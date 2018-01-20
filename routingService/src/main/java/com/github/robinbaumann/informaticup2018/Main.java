package com.github.robinbaumann.informaticup2018;


import com.github.robinbaumann.informaticup2018.database.api.IRepository;
import com.github.robinbaumann.informaticup2018.database.impl.Repository;
import com.github.robinbaumann.informaticup2018.routing.api.IPricePredictionService;
import com.github.robinbaumann.informaticup2018.routing.impl.FixedGasStationStrategy;
import com.github.robinbaumann.informaticup2018.routing.impl.PricePredictionService;
import com.github.robinbaumann.informaticup2018.routing.impl.SimpleRoutingService;
import com.github.robinbaumann.informaticup2018.webservice.Router;
import com.github.robinbaumann.informaticup2018.webservice.StationSparkProxy;
import spark.Spark;

class Main {
    public static void main(String[] args) {
        Spark.staticFiles.location("/public");
        // poor mans DI
        IRepository repository = new Repository();
        IPricePredictionService pricePredictionService = new PricePredictionService(repository);
        StationSparkProxy stationSparkProxy =
                new StationSparkProxy(
                        new SimpleRoutingService(new FixedGasStationStrategy(pricePredictionService), repository),
                        pricePredictionService,
                        repository
                );
        Router router = new Router(stationSparkProxy);
        router.setupRouter();
    }
}
