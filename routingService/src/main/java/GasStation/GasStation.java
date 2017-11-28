package GasStation;

import java.time.LocalTime;

@SuppressWarnings("WeakerAccess")
public class GasStation extends AbstractStation {
    public double lon;
    public double lat;
    public String station_name;
    public double cost;
    public int id;
    public String street;
    public String brand;
    public String house_number;
    public String zip_code;
    public String city;


    @Override
    public String toString() {
        return String.format("GasStation{lon=%s, lat=%s, station_name='%s', street='%s', brand='%s', house_number='%s', zip_code=%s, id=%d, city='%s'}", lon, lat, station_name, street, brand, house_number, zip_code, id, city);
    }


    @Override
    public void setPredictedCost(LocalTime t) {
        cost = 1;
    }
}
