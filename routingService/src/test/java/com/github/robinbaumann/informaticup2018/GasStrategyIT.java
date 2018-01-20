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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GasStrategyIT {
    private final static Gson GSON = Converters.registerOffsetDateTime(new GsonBuilder()).create();
    private final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ssZ")
            .withLocale(Locale.GERMANY).withZone(ZoneId.systemDefault());

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
        RouteRequest request = parseCsv("Bertha Benz Memorial Route");
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

    private RouteRequest parseCsv(String nameWithoutExt) throws IOException {
        List<String> lines = new ArrayList<>();
        try (InputStream stream = getClass().getResourceAsStream(nameWithoutExt + ".csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String readLine;
            while ((readLine = reader.readLine()) != null) {
                lines.add(readLine);
            }
        }
        int capa = Integer.parseInt(lines.get(0));
        List<RoutePoint> stops = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(";");
            //can't handle this format otherwise...
            String date = parts[0].replace(" ", "T") + ":00";
            OffsetDateTime time = OffsetDateTime.parse(date);
            int stationId = Integer.parseInt(parts[1]);
            stops.add(new RoutePoint(stationId, time));
        }
        return new RouteRequest(capa, stops);
    }
}


