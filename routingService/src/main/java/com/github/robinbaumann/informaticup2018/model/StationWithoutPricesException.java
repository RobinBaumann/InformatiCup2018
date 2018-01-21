package com.github.robinbaumann.informaticup2018.model;

public class StationWithoutPricesException extends Exception {
    private String unknownLevel;

    public StationWithoutPricesException(String unknownLevel) {
        this.unknownLevel = unknownLevel;
    }

    public String getUnknownLevel() {
        return unknownLevel;
    }
}
