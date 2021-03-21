package mdvrp;

public class Depot extends Customer {
    private double maxDuration = 0;
    private int maxVehicleLoad = 0;

    public double getMaxDuration() {
        return maxDuration;
    }

    void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration == 0 ? Double.POSITIVE_INFINITY : maxDuration;
    }

    public int getMaxVehicleLoad() {
        return maxVehicleLoad;
    }

    void setMaxVehicleLoad(int maxVehicleLoad) {
        this.maxVehicleLoad = maxVehicleLoad;
    }

    @Override
    public String toString() {
        return String.format("Depot(%4f\t%4d, %s)",
                maxDuration, maxVehicleLoad, super.toString());
    }
}
