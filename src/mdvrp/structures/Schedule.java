package mdvrp.structures;

import mdvrp.Customer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Schedule extends ArrayList<CustomerSequence> implements Iterable<CustomerSequence> {

    public Schedule() {
        super();
    }

    public Schedule(int size) {
        super(size);
    }

    public Schedule(List<CustomerSequence> routes) {
        super(routes);
    }

    public boolean remove(Integer customerID) {
        for (CustomerSequence route : this) {
            if (route.remove(customerID))
                return true;
        }
        return false;
    }

    // TODO: remove the mysteriousness from the naming
    public CustomerSequence underlyingGeneString() {
        return this.stream().flatMap(List::stream).collect(Collectors.toCollection(CustomerSequence::new));
    }

    public Stream<Stream<Customer>> streamCustomerStreams(Map<Integer, Customer> customers) {
        return stream().map(route -> route.streamCustomers(customers));
    }

    public Schedule deepCopy() {
        Schedule copy = new Schedule(this.size());
        for (CustomerSequence singleRouteOriginal : this) {
            copy.add(new CustomerSequence(singleRouteOriginal));
        }
        return copy;
    }
}
