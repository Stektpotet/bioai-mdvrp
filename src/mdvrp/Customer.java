package mdvrp;

public class Customer {
    private int id, x, y, demand;
    private double duration;
    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getDuration() {
        return duration;
    }

    public int getDemand() {
        return demand;
    }

    void setId(int id) {
        this.id = id;
    }

    void setX(int x) {
        this.x = x;
    }

    void setY(int y) {
        this.y = y;
    }

    void setDuration(int duration) { // TODO: if duration = 0 -> adds a lot of unnecessary computation
        this.duration = duration;
    }

    void setDemand(int demand) {
        this.demand = demand;
    }

    @Override
    public String toString() {
        return String.format("Customer(%4d\t%4d\t%4d\t%4d\t%4d)", id, x, y, duration, demand);
    }
}
