package com.github.robinbaumann.informaticup2018.routing.api;

import com.github.robinbaumann.informaticup2018.model.GasStation;
import com.github.robinbaumann.informaticup2018.model.PricePredictionRequests;
import com.github.robinbaumann.informaticup2018.model.PricePredictions;
import com.github.robinbaumann.informaticup2018.model.StationWithoutPricesException;

import java.time.OffsetDateTime;

public interface IPricePredictionService {
    int getPrice(GasStation station, OffsetDateTime timestamp, OffsetDateTime momentKnown) throws StationWithoutPricesException;

    PricePredictions predict(PricePredictionRequests requests) throws StationWithoutPricesException;
}
