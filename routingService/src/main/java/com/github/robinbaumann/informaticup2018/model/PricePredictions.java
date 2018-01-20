package com.github.robinbaumann.informaticup2018.model;

import java.util.List;

public class PricePredictions {
    private final List<PricePrediction> predictions;

    public PricePredictions(List<PricePrediction> predictions) {
        this.predictions = predictions;
    }

    public List<PricePrediction> getPredictions() {
        return predictions;
    }
}
