package com.github.robinbaumann.informaticup2018.database.api;

import com.github.robinbaumann.informaticup2018.model.GasStation;
import com.github.robinbaumann.informaticup2018.model.StationNotFoundException;

import java.time.OffsetDateTime;
import java.util.List;

public interface IRepository {
    List<GasStation> getStationsByIds(List<Integer> ids);

    GasStation getStationById(int id) throws StationNotFoundException;

    int getPrice(int stationId, OffsetDateTime timestamp);
}
