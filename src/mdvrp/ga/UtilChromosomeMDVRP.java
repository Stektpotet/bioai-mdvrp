package mdvrp.ga;

import mdvrp.Customer;
import mdvrp.Depot;
import mdvrp.MDVRP;
import mdvrp.structures.CustomerSequence;
import mdvrp.structures.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UtilChromosomeMDVRP {

    static Schedule reinsert(MDVRP problem, Depot depot, Schedule schedule, CustomerSequence toReinsert, double pChooseBestLocation) {
        Map<Integer, Customer> customers = problem.getCustomers();
        Schedule copy = schedule.deepCopy();

        for (var toReinsertId : toReinsert) {
            Customer customerToReinsert = customers.get(toReinsertId);
            // take customer out of depotSchedule
            if (!copy.remove(toReinsertId))
                continue;

            // Initialisation with zero is necessary for compilation (used only if list is not empty)
            InsertionLocation bestFeasibleLocation = new InsertionLocation(0, 0);
            List<InsertionLocation> feasibleLocations = new ArrayList<>();
            double bestAdditionalDuration = Double.MAX_VALUE;

            // for each locations in depotSchedule:
            for (int routeIndex = 0; routeIndex < copy.size(); routeIndex++) {
                CustomerSequence route = copy.get(routeIndex);
                if (!RouteScheduler.isInsertionDemandFeasible(route.streamCustomers(customers), customerToReinsert, depot))
                    continue;

                double duration = RouteScheduler.getRouteDuration(depot, route.streamCustomers(customers));
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
                insertionRoute = copy.get(insertionLocation.getRouteIndex());
                insertionLocationIndex = insertionLocation.getCustomerIndex();
            } else {
                //randomly choose route and location within route
//                System.out.println("Everything unfeasible! Selecting random insertion route!");

                insertionRoute = Util.randomChoice(copy);
                insertionLocationIndex = Util.random.nextInt(insertionRoute.size());
            }
            insertionRoute.add(insertionLocationIndex, toReinsertId);
//            System.out.println(String.format("original: %f", RouteScheduler.getScheduleDuration(depot, schedule.streamCustomerStreams(customers))));
//            System.out.println(String.format("copy:     %f", RouteScheduler.getScheduleDuration(depot, copy.streamCustomerStreams(customers))));
        }
        return copy;
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
}
