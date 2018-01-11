package Model;


import java.time.OffsetDateTime;

public class GasStop {
    private OffsetDateTime timestamp;
    private final GasStation station;
    private double amount;
    private int price;

    public GasStop(OffsetDateTime timestamp, GasStation station, double amount, int price) {
        this.timestamp = timestamp;
        this.station = station;
        this.amount = amount;
        this.price = price;
    }

    public GasStop(OffsetDateTime timestamp, GasStation station) {
        this.timestamp = timestamp;
        this.station = station;
    }

    public GasStop(GasStation station, int price) {
        this.station = station;
        this.price = price;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public GasStation getStation() {
        return station;
    }

    public double getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
