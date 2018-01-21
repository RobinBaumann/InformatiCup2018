package com.github.robinbaumann.informaticup2018;


import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.sparkjava.test.SparkServer;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.github.robinbaumann.informaticup2018.database.impl.Repository;
import com.github.robinbaumann.informaticup2018.model.*;
import com.github.robinbaumann.informaticup2018.routing.impl.FixedGasStationStrategy;
import com.github.robinbaumann.informaticup2018.routing.impl.PricePredictionService;
import com.github.robinbaumann.informaticup2018.routing.impl.RoutingStrategy;
import com.github.robinbaumann.informaticup2018.routing.impl.SimpleRoutingService;
import com.github.robinbaumann.informaticup2018.webservice.ApiHandler;
import com.github.robinbaumann.informaticup2018.webservice.Router;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.ClassRule;
import org.junit.Test;
import spark.servlet.SparkApplication;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GasStrategyAndPredictionIT {
    protected final static Gson GSON = Converters.registerOffsetDateTime(new GsonBuilder()).create();

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
            = new SparkServer<>(GasStrategyAndPredictionIT.TestSparkApp.class, 9090);

    @Test
    public void a_ok_with_bertha_benz() throws IOException, HttpClientException {
        RouteRequest request = CsvParsing.parseRouteCsv("Bertha Benz Memorial Route", getClass());
        HttpResponse response = postRequest(request);
        assertThat(response.code(), is(200));
        String json = new String(response.body());
        GasStrategy gasStrategy = GSON.fromJson(json, GasStrategy.class);
        assertThat(gasStrategy.getStops().size(), is(31));
        for (int i = 0; i < request.getRoutePoints().size(); i++) {
            assertThat(gasStrategy.getStops().get(i).getTimestamp(), is(request.getRoutePoints().get(i).getTimestamp()));
        }
    }

    @Test
    public void last_station_empty_bertha_benz() throws IOException, HttpClientException {
        RouteRequest request = CsvParsing.parseRouteCsv("Bertha Benz Memorial Route", getClass());
        HttpResponse response = postRequest(request);
        assertThat(response.code(), is(200));
        String json = new String(response.body());
        GasStrategy gasStrategy = GSON.fromJson(json, GasStrategy.class);
        List<GasStation> stations = RoutingStrategy.mapStations(gasStrategy.getStops());
        double fuel = 0;
        for (int i = 0; i < stations.size(); i++) {
            fuel += gasStrategy.getStops().get(i).getAmount();
            if (i > 0) {
                fuel -= RoutingStrategy.distanceGasStation(stations.get(i - 1), stations.get(i)) * RoutingStrategy.LITREPERKM;
            }
        }
        assertEquals(fuel,0,0.0001);
    }


    @Test
    public void fails_gracefully_with_negative_capacity() throws IOException, HttpClientException {
        fails_gracefully("bertha_negative_capacity", 400, ProblemResponse.CAPACITY_INVALID);
    }

    @Test
    public void fails_gracefully_with_stops_out_of_order() throws IOException, HttpClientException {
        fails_gracefully("bertha_out_of_order", 400, ProblemResponse.STOPS_OUT_OF_ORDER);
    }

    @Test
    public void fails_gracefully_with_non_existent_station() throws IOException, HttpClientException {
        fails_gracefully("bertha_bogus_station", 404, ProblemResponse.STATION_NOT_FOUND);
    }

    @Test
    public void fails_gracefully_with_empty_route() throws IOException, HttpClientException {
        fails_gracefully("bertha_empty", 400, ProblemResponse.EMPTY_ROUTE);
    }

    @Test
    public void fails_gracefully_with_too_small_capacity() throws IOException, HttpClientException {
        fails_gracefully("not-reachable", 400, ProblemResponse.STATION_NOT_REACHABLE);
    }

    @Test
    public void fixme() throws IOException, HttpClientException {
        fails_gracefully("bertha_stations_without_prices", 400, ProblemResponse.STATION_WITHOUT_PRICE);
    }

    @Test
    public void fails_gracefully_with_invalid_data() throws HttpClientException {
        PostMethod post = testServer.post(
                Router.API_PREFIX + Router.GASSTRAT_ROUTE,
                "invalid",
                false
        );
        HttpResponse response = testServer.execute(post);
        assertThat(response.code(), is(500));
        ProblemResponse problem = GSON.fromJson(new String(response.body()), ProblemResponse.class);
        assertThat(problem.getStatus(), is(500));
        assertThat(problem.getType(), is(ProblemResponse.INTERNAL_ERROR));
    }

    @Test
    public void a_ok_with_price_prediction() throws IOException, HttpClientException {
        PricePredictionRequests requests = CsvParsing.parsePredictionCsv("price-prediction", getClass());
        HttpResponse response = postRequest(requests);
        assertThat(response.code(), is(200));
        String json = new String(response.body());
        PricePredictions pricePredictions = GSON.fromJson(json, PricePredictions.class);
        assertThat(pricePredictions.getPredictions().size(), is(4));
        for (int i = 0; i < requests.getPredictionRequests().size(); i++) {
            assertThat(
                    pricePredictions.getPredictions().get(i).getMomentKnownPrices(),
                    is(requests.getPredictionRequests().get(i).getMomentKnownPrices()));
            assertThat(
                    pricePredictions.getPredictions().get(i).getMomentPrediction(),
                    is(requests.getPredictionRequests().get(i).getMomentPrediction()));
            assertThat(
                    pricePredictions.getPredictions().get(i).getStation().getId(),
                    is(requests.getPredictionRequests().get(i).getStationId()));
            assertThat(
                    pricePredictions.getPredictions().get(i).getPrice(),
                    is(lessThan(2000)));
            assertThat(
                    pricePredictions.getPredictions().get(i).getPrice(),
                    is(greaterThan(900)));
        }
    }

    @Test
    public void uses_actual_price_if_allowed() throws IOException, HttpClientException {
        HttpResponse response = postPredCsv("price-prediction-historic");
        assertThat(response.code(), is(200));
        String json = new String(response.body());
        PricePredictions predictions = GSON.fromJson(json, PricePredictions.class);
        assertThat(predictions.getPredictions().size(), is(1));
        assertThat(predictions.getPredictions().get(0).getPrice(), is(1309));
    }

    @Test
    public void uses_actual_price_if_momentKnown_close_to_predictionMoment() throws IOException, HttpClientException {
        HttpResponse response = postPredCsv("price-prediction-very-close");
        assertThat(response.code(), is(200));
        String json = new String(response.body());
        PricePredictions predictions = GSON.fromJson(json, PricePredictions.class);
        assertThat(predictions.getPredictions().size(), is(1));
        assertThat(predictions.getPredictions().get(0).getPrice(), is(1309));
    }

    private HttpResponse postPredCsv(String csvName) throws IOException, HttpClientException {
        PricePredictionRequests requests = CsvParsing.parsePredictionCsv(csvName, getClass());
        return postRequest(requests);
    }

    private HttpResponse postRequest(PricePredictionRequests request) throws HttpClientException {
        PostMethod post = testServer.post(
                Router.API_PREFIX + Router.PRICEPRED_ROUTE,
                GSON.toJson(request),
                false
        );
        return testServer.execute(post);
    }

    private void fails_gracefully(String csvName, int status, String type) throws IOException, HttpClientException {
        HttpResponse response = postCsv(csvName);
        String json = new String(response.body());
        ProblemResponse problemResponse = GSON.fromJson(json, ProblemResponse.class);
        assertThat(response.code(), is(status));
        assertThat(problemResponse.getStatus(), is(status));
        assertThat(problemResponse.getType(), is(type));
    }

    private HttpResponse postCsv(String csvName) throws HttpClientException, IOException {
        RouteRequest request = CsvParsing.parseRouteCsv(csvName, getClass());
        return postRequest(request);
    }

    private HttpResponse postRequest(RouteRequest request) throws HttpClientException {
        PostMethod post = testServer.post(
                Router.API_PREFIX + Router.GASSTRAT_ROUTE,
                GSON.toJson(request),
                false
        );
        return testServer.execute(post);
    }
}


