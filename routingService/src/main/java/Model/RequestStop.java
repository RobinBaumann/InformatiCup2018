package Model;

import Validation.JsonRequired;

public class RequestStop {
    @JsonRequired
    int timestamp;

    @JsonRequired
    int station_id;
    double price = 0;
    double fillIn = 0;
}
