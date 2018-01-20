package com.github.robinbaumann.informaticup2018.model;

@SuppressWarnings("WeakerAccess")
public class GasStation {
    private double lon;
    private double lat;
    private String station_name;
    private int id;
    private String street;
    private String brand;
    private String house_number;
    private String zip_code;
    private String city;
    private int bland_no;
    private int brand_no;
    private String kreis;
    private Integer abahn_id;
    private Integer bstr_id;
    private Integer sstr_id;

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

    public int getBland_no() {
        return bland_no;
    }

    public int getBrand_no() {
        return brand_no;
    }

    public String getKreis() {
        return kreis;
    }

    public Integer getAbahn_id() {
        return abahn_id;
    }

    public Integer getBstr_id() {
        return bstr_id;
    }

    public Integer getSstr_id() {
        return sstr_id;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }
}
