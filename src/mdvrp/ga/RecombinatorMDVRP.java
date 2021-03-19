package mdvrp.ga;

import ga.change.Recombinator;
import mdvrp.Customer;
import mdvrp.Depot;
import mdvrp.MDVRP;
import mdvrp.structures.CustomerSequence;
import mdvrp.structures.Schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RecombinatorMDVRP implements Recombinator<ChromosomeMDVRP> {

    private MDVRP problem;

    public RecombinatorMDVRP(MDVRP problem) {
        this.problem = problem;
    }

    @Override
    public List<ChromosomeMDVRP> recombine(List<ChromosomeMDVRP> parents) {
        return null;
    }

    @Override
    public List<ChromosomeMDVRP> crossover(ChromosomeMDVRP mum, ChromosomeMDVRP dad) {
        Map<Integer, Schedule> mSolution = mum.getSolution(problem);
        Map<Integer, Schedule> dSolution = dad.getSolution(problem);

        // choose depot (same for both)
        Integer depotId = Util.randomChoice(new ArrayList<>(mSolution.keySet()));
        Depot depot = problem.getDepots().get(depotId);
        Schedule mDepotRoutes = mSolution.get(depotId);
        Schedule dDepotRoutes = dSolution.get(depotId);

        // choose route (separately for both)
        CustomerSequence mSelectedRoute = Util.randomChoice(mDepotRoutes);
        CustomerSequence dSelectedRoute = Util.randomChoice(dDepotRoutes);

        // remove and reinsert customers from selected route in the opposite schedule
        Schedule dauDepotRoutes = reinsert(depot, mDepotRoutes, dSelectedRoute);
        Schedule sonDepotRoutes = reinsert(depot, dDepotRoutes, mSelectedRoute);

        // make new Chromosomes
        Map<Integer, Schedule> daughterSchedule = Util.deepCopySchedule(mSolution);
        daughterSchedule.put(depotId, dauDepotRoutes);
        ChromosomeMDVRP daughter = new ChromosomeMDVRP(daughterSchedule);

        Map<Integer, Schedule> sonSchedule = Util.deepCopySchedule(dSolution);
        sonSchedule.put(depotId, sonDepotRoutes);
        ChromosomeMDVRP son = new ChromosomeMDVRP(sonSchedule);


        List<ChromosomeMDVRP> offspring = new ArrayList<>();
        offspring.add(son);
        offspring.add(daughter);
        return offspring;

    }

    private Schedule reinsert(Depot depot, Schedule schedule, CustomerSequence toReinsert) {
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

                    double additionalDuration = Util.duration(before, customerToReinsert, after);

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
                if (Util.random.nextFloat() < 0.8) {
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
        }
//        System.out.println(String.format("original: %f", RouteScheduler.getScheduleDuration(depot, schedule.streamCustomerStreams(customers))));
//        System.out.println(String.format("copy:     %f", RouteScheduler.getScheduleDuration(depot, copy.streamCustomerStreams(customers))));
        return copy;
    }

    private class InsertionLocation {
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
