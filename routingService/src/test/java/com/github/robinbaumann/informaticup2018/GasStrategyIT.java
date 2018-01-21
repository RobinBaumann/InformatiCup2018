package com.github.robinbaumann.informaticup2018;


import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.sparkjava.test.SparkServer;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.github.robinbaumann.informaticup2018.database.impl.Repository;
import com.github.robinbaumann.informaticup2018.model.GasStrategy;
import com.github.robinbaumann.informaticup2018.model.ProblemResponse;
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

import java.io.IOException;

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


