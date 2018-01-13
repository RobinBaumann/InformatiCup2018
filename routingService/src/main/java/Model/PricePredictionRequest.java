package Model;

import Validation.JsonRequired;

import java.time.OffsetDateTime;

public class PricePredictionRequest {
    @JsonRequired
    private OffsetDateTime momentKnownPrices;
    @JsonRequired
    private OffsetDateTime momentPrediction;
    @JsonRequired
    private int stationId;

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
