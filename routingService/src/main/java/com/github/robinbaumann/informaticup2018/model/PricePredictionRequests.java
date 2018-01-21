package com.github.robinbaumann.informaticup2018.model;

import java.util.List;

public class PricePredictionRequests {
    private List<PricePredictionRequest> predictionRequests;

    public PricePredictionRequests(List<PricePredictionRequest> predictionRequests) {
        this.predictionRequests = predictionRequests;
    }

    public PricePredictionRequests() {
    }

    public List<PricePredictionRequest> getPredictionRequests() {
        return predictionRequests;
    }
}
