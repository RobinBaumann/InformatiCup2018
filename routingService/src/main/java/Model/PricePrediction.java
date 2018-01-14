package Model;

import java.time.OffsetDateTime;

public class PricePrediction {
    private final OffsetDateTime momentKnownPrices;
    private final OffsetDateTime momentPrediction;
    private final GasStation station;
    private final int price;

    public PricePrediction(
            OffsetDateTime momentKnownPrices,
            OffsetDateTime momentPrediction,
            GasStation station,
            int price) {
        this.momentKnownPrices = momentKnownPrices;
        this.momentPrediction = momentPrediction;
        this.station = station;
        this.price = price;
    }

    public OffsetDateTime getMomentKnownPrices() {
        return momentKnownPrices;
    }

    public OffsetDateTime getMomentPrediction() {
        return momentPrediction;
    }

    public GasStation getStation() {
        return station;
    }

    public int getPrice() {
        return price;
    }
}
