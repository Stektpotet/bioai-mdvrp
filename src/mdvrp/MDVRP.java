package mdvrp;

import java.util.*;

/**
 * Multi-Depot Vehicle Routing Problem
 */
public class MDVRP {
    private final String name;
    private final Map<Integer, Customer> customers;
    private final Map<Integer, Depot> depots;

    private int numMaxVehicles = 0;

    MDVRP(String name, int numMaxVehicles, int numCustomers, int numDepots) {
        this.name = name;
        this.numMaxVehicles = numMaxVehicles;
        customers = new HashMap<>(numCustomers);
        depots = new HashMap<>(numDepots);
    }

    Map<Integer, Depot> getDepotsMutable() {
        return depots;
    }
    public Map<Integer, Depot> getDepots() {
        return Collections.unmodifiableMap(depots);
    }

    public int getNumMaxVehicles() {
        return numMaxVehicles;
    }

    public int getNumCustomers() {
        return customers.size();
    }
    public int getNumDepots() { return depots.size(); }

    void setNumMaxVehicles(int numMaxVehicles) {
        this.numMaxVehicles = numMaxVehicles;
    }

    Map<Integer, Customer>  getCustomersMutable() {
        return customers;
    }
    public Map<Integer, Customer> getCustomers() {
        return Collections.unmodifiableMap(customers);
    }

    public String getName() {
        return name;
    }
}
