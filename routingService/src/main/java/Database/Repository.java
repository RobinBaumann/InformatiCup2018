package Database;


import Model.GasStation;
import Model.StationNotFoundException;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

public class Repository {
    private static Sql2o sql2o = new Sql2o(ConnectionFactory.getDataSource());

    public static List<GasStation> getStationsByIds(List<Integer> ids) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("select id, lat, lon, station_name, street, brand, house_number, zip_code, city " +
                    "from stations where id in (:ids)")
                    .addParameter("ids", ids)
                    .executeAndFetch(GasStation.class);
        }
    }

    public static GasStation getStationById(int id) throws StationNotFoundException {
        try (Connection con = sql2o.open()) {
            List<GasStation> stations = con.createQuery("select id, lat, lon, station_name, street, brand, house_number, zip_code, city" +
                    "from stations where id = :id")
                    .addParameter("id", id)
                    .executeAndFetch(GasStation.class);
            if (stations.size() != 1) {
                throw new StationNotFoundException(id);
            }
            return stations.get(0);
        }
    }
}
