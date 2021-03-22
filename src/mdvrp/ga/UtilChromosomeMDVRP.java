package mdvrp.ga;

import ga.data.Chromosome;
import mdvrp.Customer;
import mdvrp.Depot;
import mdvrp.MDVRP;
import mdvrp.structures.CustomerSequence;
import mdvrp.structures.Schedule;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UtilChromosomeMDVRP {

    public static int hammingDistance(ChromosomeMDVRP a, ChromosomeMDVRP b) {
        int dist = 0;
        List<Integer> chromosomeA = new ArrayList<>();
        List<Integer> chromosomeB = new ArrayList<>();

        for (var depotID : a.getGenes().keySet()) {
            chromosomeA.addAll(a.getGenes().get(depotID));
            chromosomeB.addAll(b.getGenes().get(depotID));
        }
        for (int i = 0; i < chromosomeA.size(); i++) {
            dist += (chromosomeA.get(i).equals(chromosomeB.get(i))) ? 0 : 1;
        }
        return dist;
    }

    public static Comparator<ChromosomeMDVRP> chromosomeFitnessComparator(MDVRP problem) {
        return (a, b) -> (int) Math.signum(a.fitness(problem) - b.fitness(problem));
    }

    public static Map<Integer, Schedule> deepCopySolution(Map<Integer, Schedule> original) {
        Map<Integer, Schedule> copy = new HashMap<>();

        for (Map.Entry<Integer, Schedule> depotSchedule : original.entrySet()) {

            // get depot Id
            Integer depotId = depotSchedule.getKey();

            // copy routes
            Schedule routesOriginal = depotSchedule.getValue();
            Schedule routesCopy = new Schedule(routesOriginal.size());
            for (CustomerSequence singleRouteOriginal : routesOriginal) {
                routesCopy.add(new CustomerSequence(singleRouteOriginal));
            }

            // insert into copied schedule
            copy.put(depotId, routesCopy);
        }

        return copy;
    }

    public static Map<Integer, CustomerSequence> deepCopyGenes(ChromosomeMDVRP chromosome) {
        Map<Integer, CustomerSequence> genes = chromosome.getGenes();
        Map<Integer, CustomerSequence> copy = new HashMap<>(genes.size());
        for (Map.Entry<Integer, CustomerSequence> gene : genes.entrySet()) {
            copy.put(gene.getKey(), new CustomerSequence(gene.getValue()));
        }
        return copy;
    }

    static Schedule removeAndReinsertAll(MDVRP problem, Depot depot, Schedule schedule, CustomerSequence toReinsert, double pChooseBestLocation) {
        Map<Integer, Customer> customers = problem.getCustomers();
        Schedule copy = schedule.deepCopy();

        for (var toReinsertId : toReinsert) {
            removeAndReinsert(depot, pChooseBestLocation, customers, copy, toReinsertId);
//            System.out.println(String.format("original: %f", RouteScheduler.getScheduleDuration(depot, schedule.streamCustomerStreams(customers))));
//            System.out.println(String.format("copy:     %f", RouteScheduler.getScheduleDuration(depot, copy.streamCustomerStreams(customers))));
        }
        return copy;
    }

    private static void removeAndReinsert(Depot depot, double pChooseBestLocation, Map<Integer, Customer> customers, Schedule copy, Integer toReinsertId) {
        Customer customerToReinsert = customers.get(toReinsertId);
        // take customer out of depotSchedule
        if (!copy.remove(toReinsertId))
            return;

        reinsertSingleCustomer(depot, pChooseBestLocation, customers, copy, toReinsertId, customerToReinsert);
    }

    static void reinsertSingleCustomer(Depot depot, double pChooseBestLocation, Map<Integer, Customer> customers, Schedule receiving, Integer toReinsertId, Customer customerToReinsert) {
        // Initialisation with zero is necessary for compilation (used only if list is not empty)
        InsertionLocation bestFeasibleLocation = new InsertionLocation(0, 0);
        List<InsertionLocation> feasibleLocations = new ArrayList<>();
        double bestAdditionalDuration = Double.MAX_VALUE;

        // for each locations in depotSchedule:
        for (int routeIndex = 0; routeIndex < receiving.size(); routeIndex++) {
            CustomerSequence route = receiving.get(routeIndex);
            if (!isInsertionDemandFeasible(route.streamCustomers(customers), customerToReinsert, depot))
                continue;

            double duration = routeDuration(depot, route.streamCustomers(customers));
            double availableDuration = depot.getMaxDuration() - duration;

            if (availableDuration <= 0) {
                continue;
            }

            for (int customerIndex = 0; customerIndex <= route.size(); customerIndex++) {

                // Watch out: if we get an index out of bound, think about the indices of before and after
                Customer before = customerIndex == 0 ? depot : customers.get(route.get(customerIndex - 1));
                Customer after = customerIndex == route.size() ? depot : customers.get(route.get(customerIndex));

                double additionalDuration = Util.duration(before, customerToReinsert, after) - Util.duration(before, after);

                if (additionalDuration < availableDuration) {
                    InsertionLocation insertionLocation = new InsertionLocation(routeIndex, customerIndex);
                    feasibleLocations.add(insertionLocation);

                    if (additionalDuration < bestAdditionalDuration) {
                        bestFeasibleLocation = insertionLocation;
                        bestAdditionalDuration = additionalDuration;
                    }
                }
            }
        }

        CustomerSequence insertionRoute;
        int insertionLocationIndex;

        //      if feasibleLocations is empty:
        if (!feasibleLocations.isEmpty()) {
            InsertionLocation insertionLocation;
            if (Util.random.nextFloat() < pChooseBestLocation) {
                //          choose best location in feasibleLocations
//                    System.out.println("Choosing best feasible location!");
                insertionLocation = bestFeasibleLocation;
            } else {
                //          choose random feasibleLocations
//                    System.out.println("Choosing random feasible location!");
                insertionLocation = Util.randomChoice(feasibleLocations);
            }
            insertionRoute = receiving.get(insertionLocation.getRouteIndex());
            insertionLocationIndex = insertionLocation.getCustomerIndex();
        } else {
            //randomly choose route and location within route
//                System.out.println("Everything unfeasible! Selecting random insertion route!");

            insertionRoute = Util.randomChoice(receiving);
            int inRouteS = insertionRoute.size();
            insertionLocationIndex = inRouteS == 0 ? 0 : Util.random.nextInt(inRouteS);
        }
        insertionRoute.add(insertionLocationIndex, toReinsertId);
    }

    private static class InsertionLocation {

        private final int routeIndex;
        private final int customerIndex;

        InsertionLocation(int routeIndex, int customerIndex) {
            this.routeIndex = routeIndex;
            this.customerIndex = customerIndex;
        }

        int getRouteIndex() {
            return routeIndex;
        }

        int getCustomerIndex() {
            return customerIndex;
        }
    }

    public static boolean isInsertionDemandFeasible(Stream<Customer> route, Customer insertion, Depot depot) {
        return routeDemand(route) + insertion.getDemand() <= depot.getMaxVehicleLoad();
    }

    public static double routeDuration(Depot depot, Stream<Customer> route) {
        List<Customer> routeCustomers = route.collect(Collectors.toList());

        double duration = 0;
        Customer position = depot;
        for (Customer customer : routeCustomers) {
            duration += Util.duration(position, customer);
            position = customer;
        }
        duration += Util.duration(position, depot);

        return duration;
    }

    static double scheduleDuration(Depot depot, Stream<Stream<Customer>> customerStreams) {
        return customerStreams.mapToDouble(routeStream -> routeDuration(depot, routeStream)).sum();
    }

    public static int routeDemand(Stream<Customer> customerStream) {
        return customerStream.mapToInt(Customer::getDemand).sum();
    }

    public static int scheduleDemand(Schedule depotSchedule, Map<Integer, Customer> customers) {
        return depotSchedule.stream().mapToInt(route -> routeDemand(route.streamCustomers(customers))).sum();
    }

    public static int geneStringDemand(CustomerSequence geneString, Map<Integer, Customer> customers) {
        return geneString.streamCustomers(customers).mapToInt(Customer::getDemand).sum();
    }
}
