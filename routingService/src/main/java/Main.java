import Database.JellFactory;
import WebService.Router;
import WebService.StationSparkProxy;
import com.noelherrick.jell.Jell;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        Jell jell = null;
        try {
            jell = JellFactory.setUpConnection("jdbc:postgresql://localhost:3333/infocup", "infocup","");
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Database connection failed");
        }
        StationSparkProxy stationSparkProxy = new StationSparkProxy(jell);
        Router router = new Router(stationSparkProxy);
        router.setupRouter();
    }
}
