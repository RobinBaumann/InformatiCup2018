package Model;

import Validation.JsonRequired;

import java.util.List;

public class PricePredictionRequests {
    @JsonRequired
    private List<PricePredictionRequest> predictionRequests;

    public List<PricePredictionRequest> getPredictionRequests() {
        return predictionRequests;
    }
}
