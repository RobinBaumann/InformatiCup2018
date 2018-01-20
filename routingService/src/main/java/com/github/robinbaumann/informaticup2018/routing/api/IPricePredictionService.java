package com.github.robinbaumann.informaticup2018.routing.api;

import com.github.robinbaumann.informaticup2018.model.GasStation;
import com.github.robinbaumann.informaticup2018.model.PricePredictionRequests;
import com.github.robinbaumann.informaticup2018.model.PricePredictions;

import java.time.OffsetDateTime;

public interface IPricePredictionService {
    int getPrice(GasStation station, OffsetDateTime timestamp, OffsetDateTime momentKnown);

    PricePredictions predict(PricePredictionRequests requests);
}
