package WebService;

import GasStation.AbstractStation;
import GasStation.GasStation;
import com.google.common.base.Function;
import com.noelherrick.jell.Jell;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StationSparkProxy {
    private Jell jell = null;
    private final static Logger LOGGER = Logger.getLogger(StationSparkProxy.class.getName());

    public StationSparkProxy(Jell jell) {
        this.jell = jell;
    }

    private Function<String, Collection<GasStation>> queryStationMethod = new Function<String, Collection<GasStation>>() {

        @Override
        public Collection<GasStation> apply(String s) {
            Collection<GasStation> gasStations = null;

            try {
                gasStations = jell.query(s, GasStation.class);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "SQL Exception " + e.toString());
            } catch (InstantiationException e) {
                LOGGER.log(Level.SEVERE, "Instantiation Exception " + e.toString());
            } catch (IllegalAccessException e) {
                LOGGER.log(Level.SEVERE, "Illegal Access Exception " + e.toString());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Exception " + e.toString());
            }
            return gasStations;
        }
    };

    public Collection<GasStation> get(Request request, Response response) {

        return AbstractStation.get(Integer.parseInt(request.params(":id")), queryStationMethod);
    }
}
