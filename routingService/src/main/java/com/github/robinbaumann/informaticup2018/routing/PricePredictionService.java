package com.github.robinbaumann.informaticup2018.routing;

import com.github.robinbaumann.informaticup2018.database.Repository;
import com.github.robinbaumann.informaticup2018.model.*;
import hex.genmodel.*;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.exception.PredictException;
import hex.genmodel.easy.prediction.RegressionModelPrediction;

import java.io.IOException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PricePredictionService {

    private Repository repository;
    private static EasyPredictModelWrapper model;
    static {
        URL url = PricePredictionService.class.getResource(
                "/grid_a35c4e7e_e920_4a4d_b8dd_ba515a4d986e_model_0.zip");
        try {
            MojoReaderBackend readerBackend =
                    MojoReaderBackendFactory.createReaderBackend(url, MojoReaderBackendFactory.CachingStrategy.MEMORY);
            MojoModel mojoModel = ModelMojoReader.readFrom(readerBackend);
            model = new EasyPredictModelWrapper(mojoModel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PricePredictionService(Repository repository) {
        this.repository = repository;
    }

    public int getPrice(GasStation station, OffsetDateTime timestamp, OffsetDateTime momentKnown) {
        if (timestamp.isBefore(momentKnown)) {
            //can use historic price
            return repository.getPrice(station.getId(), timestamp);
        } else {
            try {
                RowData rowData = new RowData();
                rowData.put("year", Integer.toString(timestamp.getYear()));
                rowData.put("month", Integer.toString(timestamp.getMonthValue()));
                rowData.put("day_of_week", Integer.toString(timestamp.getDayOfWeek().getValue()));
                rowData.put("hour_of_day", Integer.toString(timestamp.getHour()));
                rowData.put("day_of_month", Integer.toString(timestamp.getDayOfMonth()));
                rowData.put("station_id", Integer.toString(station.getId()));
                rowData.put("brand_no", Integer.toString(station.getBrand_no()));
                rowData.put("bland_no", Integer.toString(station.getBland_no()));
                rowData.put("kreis", station.getKreis());
                if (station.getAbahn_id() != null) {
                    rowData.put("abahn_id", station.getAbahn_id().toString());
                }
                if (station.getBstr_id() != null) {
                    rowData.put("bstr_id", station.getBstr_id().toString());
                }
                if (station.getSstr_id() != null) {
                    rowData.put("sstr_id", station.getSstr_id().toString());
                }
                RegressionModelPrediction regressionModelPrediction = model.predictRegression(rowData);
                return (int)regressionModelPrediction.value;
            } catch (PredictException e) {
                throw new RuntimeException(e);
            }
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
