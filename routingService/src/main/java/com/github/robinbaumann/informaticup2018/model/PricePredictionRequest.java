package com.github.robinbaumann.informaticup2018.model;


import java.time.OffsetDateTime;

public class PricePredictionRequest {
    private OffsetDateTime momentKnownPrices;
    private OffsetDateTime momentPrediction;
    private int stationId;

    public PricePredictionRequest(OffsetDateTime momentKnownPrices, OffsetDateTime momentPrediction, int stationId) {
        this.momentKnownPrices = momentKnownPrices;
        this.momentPrediction = momentPrediction;
        this.stationId = stationId;
    }

    public PricePredictionRequest() {
    }

    public OffsetDateTime getMomentKnownPrices() {
        return momentKnownPrices;
    }

    public OffsetDateTime getMomentPrediction() {
        return momentPrediction;
    }

    public int getStationId() {
        return stationId;
    }
}
