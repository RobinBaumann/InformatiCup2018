package com.github.robinbaumann.informaticup2018.database.impl;


import com.github.robinbaumann.informaticup2018.database.api.IRepository;
import com.github.robinbaumann.informaticup2018.model.GasStation;
import com.github.robinbaumann.informaticup2018.model.StationNotFoundException;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.time.OffsetDateTime;
import java.util.List;

public class Repository implements IRepository {
    // single instance preferred way to use sql2o
    private static Sql2o sql2o = new Sql2o(ConnectionFactory.getDataSource());

    @Override
    public List<GasStation> getStationsByIds(List<Integer> ids) {
        try (Connection con = sql2o.open()) {
            return con.createQuery(
                    "SELECT id, lat, lon, station_name, street, brand, house_number, zip_code, city, " +
                            "brand_no, bland_no, kreis, abahn_id, bstr_id, sstr_id " +
                            "FROM stations WHERE id IN (:ids)")
                    .addParameter("ids", ids)
                    .executeAndFetch(GasStation.class);
        }
    }

    @Override
    public GasStation getStationById(int id) throws StationNotFoundException {
        try (Connection con = sql2o.open()) {
            List<GasStation> stations = con.createQuery(
                    "SELECT id, lat, lon, station_name, street, brand, house_number, zip_code, city, " +
                            "brand_no, bland_no, kreis, abahn_id, bstr_id, sstr_id " +
                            "FROM stations WHERE id = :id")
                    .addParameter("id", id)
                    .executeAndFetch(GasStation.class);
            if (stations.size() != 1) {
                throw new StationNotFoundException(id);
            }
            return stations.get(0);
        }
    }

    @Override
    public int getPrice(int stationId, OffsetDateTime timestamp) {
        try (Connection con = sql2o.open()) {
            return con.createQuery(
                    "SELECT price FROM prices_sampled " +
                            "WHERE station_id = :id " +
                            "AND time_stamp < :time_stamp " +
                            "ORDER BY time_stamp DESC " +
                            "LIMIT 1"
            )
                    .addParameter("id", stationId)
                    .addParameter("time_stamp", timestamp)
                    .executeScalar(Integer.class);
        }
    }
}
