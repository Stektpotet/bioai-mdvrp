package mdvrp;

public class Depot extends Customer {
    private int maxDuration = 0;
    private int maxVehicleLoad = 0;

    public int getMaxDuration() {
        return maxDuration;
    }

    void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public int getMaxVehicleLoad() {
        return maxVehicleLoad;
    }

    void setMaxVehicleLoad(int maxVehicleLoad) {
        this.maxVehicleLoad = maxVehicleLoad;
    }

    @Override
    public String toString() {
        return String.format("Depot(%4d\t%4d, %s)",
                maxDuration, maxVehicleLoad, super.toString());
    }
}
