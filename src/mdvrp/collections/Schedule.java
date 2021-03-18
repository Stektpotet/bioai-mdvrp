package mdvrp.collections;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    // TODO: make this a todo remove the mysteriousness from the naming
    public CustomerSequence underlyingGeneString() {
        return this.stream().flatMap(List::stream).collect(Collectors.toCollection(CustomerSequence::new));
    }
}
