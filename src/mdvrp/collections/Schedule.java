package mdvrp.collections;

import mdvrp.Customer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Schedule implements Iterable<Route> {
    private List<Route> routes;

    public Schedule() {
        routes = new ArrayList<>();
    }
    public Schedule(List<List<Integer>> routes) {
        this.routes = routes.stream().map(Route::new).collect(Collectors.toList());
    }

    @Override
    public Iterator<Route> iterator() {
        return routes.iterator();
    }
    public IntStream geneStringStream() {
        return routes.stream().flatMapToInt(Route::IdStream);
    }
}
