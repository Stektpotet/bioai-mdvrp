package mdvrp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MDVRP {
    private List<Customer> vertices;
    private List<Customer> customers;
    private List<Depot> depots;

    private int numMaxVehicles = 0;
    private int numCustomers = 0;

    MDVRP(int numMaxVehicles, int numCustomers, int numDepots) {
        vertices = new ArrayList<Customer>(numCustomers + numDepots);
        customers = Stream.generate(Customer::new).limit(numCustomers).collect(Collectors.toList());
        depots = Stream.generate(Depot::new).limit(numDepots).collect(Collectors.toList());

        // Nice way to get vertices to reference the same objects stored in the customer and depot lists
        vertices.addAll(customers);
        vertices.addAll(depots);

        this.numMaxVehicles = numMaxVehicles;
        this.numCustomers = numCustomers;
    }

    List<Depot> getDepotsMutable() {
        return depots;
    }
    public List<Depot> getDepots() {
        return Collections.unmodifiableList(depots);
    }

    public int getNumMaxVehicles() {
        return numMaxVehicles;
    }

    public int getNumCustomers() {
        return numCustomers;
    }
    public int getNumDepots() { return depots.size(); }

    void setNumMaxVehicles(int numMaxVehicles) {
        this.numMaxVehicles = numMaxVehicles;
    }
    void setNumCustomers(int numCustomers) {
        this.numCustomers = numCustomers;
    }

    List<Customer> getVerticesMutable() {
        return vertices;
    }
    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(customers);
    }
}
