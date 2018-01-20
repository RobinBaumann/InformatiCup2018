package com.github.robinbaumann.informaticup2018.model;


import java.time.OffsetDateTime;

public class RoutePoint {
    private int stationId;

    private OffsetDateTime timestamp;

    // empty ctor for libs
    public RoutePoint() {
    }

    public RoutePoint(int stationId, OffsetDateTime timestamp) {
        this.stationId = stationId;
        this.timestamp = timestamp;
    }

    public int getStationId() {
        return stationId;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}
