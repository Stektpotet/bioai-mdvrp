package mdvrp.collections;

import mdvrp.Customer;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Route implements Iterable<Integer> {
    private List<Integer> customerIds;

    public Route(){
        this.customerIds = new ArrayList<>();
    }
    public Route(List<Integer> customerIds) {
        this.customerIds = customerIds;
    }

    List<Integer> getCustomerIds() {
        return customerIds;
    }

    @Override
    public Iterator<Integer> iterator() {
        return customerIds.iterator();
    }

    public IntStream IdStream() {
        return customerIds.stream().mapToInt(Integer::intValue);
    }
    public Stream<Customer> streamCustomers(Map<Integer, Customer> customers) {
        return customerIds.stream().map(customers::get);
    }
}
