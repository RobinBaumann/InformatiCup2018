package com.github.robinbaumann.informaticup2018;

import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.sparkjava.test.SparkServer;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.github.robinbaumann.informaticup2018.database.impl.Repository;
import com.github.robinbaumann.informaticup2018.model.PricePredictionRequests;
import com.github.robinbaumann.informaticup2018.model.PricePredictions;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

public class PricePredictionIT {
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
            = new SparkServer<>(PricePredictionIT.TestSparkApp.class, 9090);
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

    private HttpResponse postRequest(PricePredictionRequests request) throws HttpClientException {
        PostMethod post = testServer.post(
                Router.API_PREFIX + Router.PRICEPRED_ROUTE,
                GSON.toJson(request),
                false
        );
        return testServer.execute(post);
    }
}
