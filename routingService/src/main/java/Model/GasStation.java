package Model;

@SuppressWarnings("WeakerAccess")
public class GasStation {
    public double lon;
    public double lat;
    public String station_name;
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

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public String getStation_name() {
        return station_name;
    }

    public int getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getBrand() {
        return brand;
    }

    public String getHouse_number() {
        return house_number;
    }

    public String getZip_code() {
        return zip_code;
    }

    public String getCity() {
        return city;
    }
}
