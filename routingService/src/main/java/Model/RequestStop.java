package Model;


public class RequestStop {
    private int timestamp;
    private int station_id;
    private double price;
    private double fillIn;

    public int getTimestamp() {
        return timestamp;
    }

    public int getStation_id() {
        return station_id;
    }

    public double getPrice() {
        return price;
    }

    public double getFillIn() {
        return fillIn;
    }
}
