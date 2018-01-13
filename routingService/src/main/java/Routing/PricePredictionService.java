package Routing;

import Database.Repository;
import Model.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PricePredictionService {

    private Repository repository;

    public PricePredictionService(Repository repository) {
        this.repository = repository;
    }

    public int getPrice(GasStation station, OffsetDateTime timestamp, OffsetDateTime momentKnown) {
        if (timestamp.isBefore(momentKnown)) {
            //can use historic price
            return repository.getPrice(station.getId(), timestamp);
        } else {
            //TODO implement
            return (int) (Math.random() * 2000);
        }
    }

    public PricePredictions predict(PricePredictionRequests requests) {
        List<Integer> ids = requests.getPredictionRequests().stream()
                .map(PricePredictionRequest::getStationId)
                .collect(Collectors.toList());
        Map<Integer, GasStation> stations = repository.getStationsByIds(ids).stream()
                .collect(Collectors.toMap(GasStation::getId, Function.identity()));
        List<PricePrediction> predictions = new ArrayList<>();
        for (PricePredictionRequest request : requests.getPredictionRequests()) {
            GasStation station = stations.get(request.getStationId());
            predictions.add(new PricePrediction(
                    request.getMomentKnownPrices(),
                    request.getMomentPrediction(),
                    station,
                    getPrice(station, request.getMomentPrediction(), request.getMomentKnownPrices())
            ));
        }
        return new PricePredictions(predictions);
    }
}
