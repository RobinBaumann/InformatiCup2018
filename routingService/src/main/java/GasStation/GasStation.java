package GasStation;

import java.time.LocalTime;

public class GasStation extends AbstractStation {
    public Double lon;
    public Double lat;
    public String station_name;
    public String street;
    public String brand;
    public int id;
    public String house_number;
    public String zip_code;
    public String city;
    public double cost;

    @Override
    public String toString() {
        return "GasStation{" +
                "lon=" + lon +
                ", lat=" + lat +
                ", station_name='" + station_name + '\'' +
                ", street='" + street + '\'' +
                ", brand='" + brand + '\'' +
                ", house_number='" + house_number + '\'' +
                ", zip_code=" + zip_code +
                ", id=" + id +
                ", city='" + city + '\'' +
                '}';
    }


    @Override
    public void setPredictedCost(LocalTime t) {
        cost = 1;
    }
}
