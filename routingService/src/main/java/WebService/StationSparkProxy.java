package WebService;

import GasStation.AbstractStation;
import GasStation.GasStation;
import Model.RequestStop;
import Validation.AnnotatedDeserializer;
import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
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
    // Query function
    private final Function<String, Collection<GasStation>> queryStationMethod = (Function<String, Collection<GasStation>>) s -> {
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
    };

    public StationSparkProxy(Jell jell) {
        this.jell = jell;
    }

    /**
     * Retrieve Station by ID
     * @param request
     * @param response
     * @return
     */
    public Collection<GasStation> getStationByID(Request request, Response response) {
        return AbstractStation.getByID(Integer.parseInt(request.params(":id")), queryStationMethod);
    }

    /**
     * TODO:
     * Create Route and solve Fixed Path Station Problem
     * @param request
     * @param response
     * @return
     */
    public String getStationsByRoute(Request request, Response response) {
        String route = (request.queryParams("route"));
        Gson gson = new GsonBuilder()
                        .registerTypeAdapter(RequestStop.class, new AnnotatedDeserializer<RequestStop>())
                        .create();
        String responseString = "Syntax Error";
        try {
            //TODO fix types
            RequestStop[] r = gson.fromJson(route, RequestStop[].class);
            responseString = r.toString();
        } catch (JsonSyntaxException e) {
            LOGGER.log(Level.SEVERE, "JSON Syntax Error " + e.toString());
        }

        return responseString;
    }
}
