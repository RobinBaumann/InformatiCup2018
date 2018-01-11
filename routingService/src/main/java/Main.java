import Database.ConnectionFactory;
import WebService.Router;
import WebService.StationSparkProxy;
import com.noelherrick.jell.Jell;
import spark.Spark;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

class Main {
    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Spark.staticFiles.location("/public");
        StationSparkProxy stationSparkProxy = new StationSparkProxy();
        Router router = new Router(stationSparkProxy);
        router.setupRouter();
    }
}
