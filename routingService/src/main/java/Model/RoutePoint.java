package Model;

import Validation.JsonRequired;

import java.time.OffsetDateTime;

public class RoutePoint {
    @JsonRequired
    int stationId;

    @JsonRequired
    OffsetDateTime timestamp;

    public int getStationId() {
        return stationId;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}
