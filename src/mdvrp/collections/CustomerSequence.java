package mdvrp.collections;

import mdvrp.Customer;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CustomerSequence extends ArrayList<Integer> implements Iterable<Integer> {

    public CustomerSequence(){
        super();
    }

    public CustomerSequence(List<Integer> customerIds) {
        super(customerIds);
    }

    IntStream IdStream() {
        return this.stream().mapToInt(Integer::intValue);
    }

    public Stream<Customer> streamCustomers(Map<Integer, Customer> customers) {
        return this.stream().map(customers::get);
    }
}
