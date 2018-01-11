package Database;


import GasStation.GasStation;
import com.noelherrick.jell.Jell;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Repository {
    private final static Logger LOGGER = Logger.getLogger(Repository.class.getName());

    public static List<GasStation> getStationsByIds(List<Integer> ids) {
        List<GasStation> stations;
        try (Connection con = ConnectionFactory.getConnection()) {
            Jell jell = new Jell(con);
            stations = new ArrayList<>(jell.query("select id, lat, lon, station_name, street, brand, house_number, zip_code, city" +
                    "from stations where id in (@ids)", GasStation.class, ids));
        } catch (IllegalAccessException | NoSuchFieldException | SQLException | InstantiationException e) {
            LOGGER.severe(e.toString());
            throw new RuntimeException();
        }
        return stations;
    }
}
