package com.github.robinbaumann.informaticup2018;


import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.sparkjava.test.SparkServer;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.github.robinbaumann.informaticup2018.database.impl.Repository;
import com.github.robinbaumann.informaticup2018.model.GasStrategy;
import com.github.robinbaumann.informaticup2018.model.RoutePoint;
import com.github.robinbaumann.informaticup2018.model.RouteRequest;
import com.github.robinbaumann.informaticup2018.routing.impl.FixedGasStationStrategy;
import com.github.robinbaumann.informaticup2018.routing.impl.PricePredictionService;
import com.github.robinbaumann.informaticup2018.routing.impl.SimpleRoutingService;
import com.github.robinbaumann.informaticup2018.webservice.ApiHandler;
import com.github.robinbaumann.informaticup2018.webservice.Router;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.ClassRule;
import org.junit.Test;
import spark.servlet.SparkApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GasStrategyIT {
    private final static Gson GSON = Converters.registerOffsetDateTime(new GsonBuilder()).create();

    public static class TestSparkApp implements SparkApplication {
        @Override
        public void init() {
            Repository repository = new Repository();
            PricePredictionService pricePredictionService = new PricePredictionService(repository);
            SimpleRoutingService simpleRoutingService =
                    new SimpleRoutingService(new FixedGasStationStrategy(pricePredictionService), repository);
            Router router = new Router(new ApiHandler(simpleRoutingService, pricePredictionService, repository));
            router.setupRouter();
        }
    }

    @ClassRule
    public static SparkServer<TestSparkApp> testServer
            = new SparkServer<>(GasStrategyIT.TestSparkApp.class, 9090);

    @Test
    public void a_ok_with_bertha_benz() throws IOException, HttpClientException {
        RouteRequest request = CsvParsing.parseCsv("Bertha Benz Memorial Route", getClass());
        PostMethod post = testServer.post(
                Router.API_PREFIX + Router.GASSTRAT_ROUTE,
                GSON.toJson(request),
                false);
        HttpResponse response = testServer.execute(post);
        assertThat(response.code(), is(200));
        String json = new String(response.body());
        GasStrategy gasStrategy = GSON.fromJson(json, GasStrategy.class);
        assertThat(gasStrategy.getStops().size(), is(31));
        for (int i = 0; i < request.getRoutePoints().size(); i++) {
            assertThat(gasStrategy.getStops().get(i).getTimestamp(), is(request.getRoutePoints().get(i).getTimestamp()));
        }
    }

}


