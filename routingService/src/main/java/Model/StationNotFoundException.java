package Model;

public class StationNotFoundException extends Exception {
    private int id;

    public StationNotFoundException(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
