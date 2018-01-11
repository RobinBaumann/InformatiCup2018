package Routing;

import Model.GasStation;

import java.time.OffsetDateTime;

public class PricePredictionService {
    public int getPrice(GasStation station, OffsetDateTime timestamp) {
        //TODO implement
        return (int)(Math.random() * 2000);
    }
}
