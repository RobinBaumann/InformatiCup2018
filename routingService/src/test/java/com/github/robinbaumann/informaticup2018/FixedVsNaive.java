package com.github.robinbaumann.informaticup2018;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.github.robinbaumann.informaticup2018.database.impl.Repository;
import com.github.robinbaumann.informaticup2018.model.GasStop;
import com.github.robinbaumann.informaticup2018.model.GasStrategy;
import com.github.robinbaumann.informaticup2018.model.RouteRequest;
import com.github.robinbaumann.informaticup2018.routing.impl.FixedGasStationStrategy;
import com.github.robinbaumann.informaticup2018.routing.impl.NaiveStrategy;
import com.github.robinbaumann.informaticup2018.routing.impl.PricePredictionService;
import com.github.robinbaumann.informaticup2018.routing.impl.SimpleRoutingService;
import com.github.robinbaumann.informaticup2018.webservice.ApiHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import spark.Request;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FixedVsNaive {
    private final static Gson GSON = Converters.registerOffsetDateTime(new GsonBuilder()).create();

    private static ApiHandler fixedHandler;
    private static ApiHandler naiveHandler;

    static {
        Repository repository = new Repository();
        PricePredictionService pricePredictionService = new PricePredictionService(repository);
        SimpleRoutingService simpleRoutingService =
                new SimpleRoutingService(new FixedGasStationStrategy(pricePredictionService), repository);
        fixedHandler = new ApiHandler(simpleRoutingService, pricePredictionService, repository);
        SimpleRoutingService naiveRoutingService =
                new SimpleRoutingService(new NaiveStrategy(pricePredictionService), repository);
        naiveHandler = new ApiHandler(naiveRoutingService, pricePredictionService, repository);
    }


    @Test
    public void fixed_knows_bertha_better_than_naive() throws IOException {
        RouteRequest request = CsvParsing.parseCsv("Bertha Benz Memorial Route", getClass());
        Request mock = mock(Request.class);
        when(mock.body()).thenReturn(GSON.toJson(request));
        GasStrategy fixed = GSON.fromJson(fixedHandler.getStationsByRoute(mock, null), GasStrategy.class);
        GasStrategy naive = GSON.fromJson(naiveHandler.getStationsByRoute(mock, null), GasStrategy.class);
        double fixedPrice = 0;
        double naivePrice = 0;
        for (GasStop stop : fixed.getStops()) {
            fixedPrice += stop.getPrice() * stop.getAmount();
        }
        for (GasStop stop : naive.getStops()) {
            naivePrice += stop.getPrice() * stop.getAmount();
        }
        assertThat(fixedPrice, lessThan(naivePrice));
    }
}
