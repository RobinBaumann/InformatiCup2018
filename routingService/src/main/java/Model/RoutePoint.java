package Model;

import Validation.JsonRequired;

import java.time.OffsetDateTime;

public class RoutePoint {
    @JsonRequired
    int stationId;

    @JsonRequired
    OffsetDateTime timestamp;
}
