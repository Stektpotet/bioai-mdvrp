package mdvrp.ga;

import ga.data.Chromosome;
import mdvrp.Customer;
import mdvrp.MDVRP;
import mdvrp.MDVRPFiles;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChromosomeMDVRP implements Chromosome {
    // TODO: PLEASE GOD NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO

    public static final MDVRP PROBLEM = MDVRPFiles.ReadFile("res/problems/p01");

    private boolean feasible = true;
    private Map<Integer, List<Integer>> genes;
    private boolean scheduled = false;
    private Map<Integer, List<List<Integer>>> schedule;
    private static final float UNFEASABILITY_FEE = 1000;

    ChromosomeMDVRP(Map<Integer, List<Integer>> customersPerDepot, boolean shuffle) {
        // https://stackoverflow.com/questions/8559092/create-an-array-of-arraylists
        genes = customersPerDepot;
        if (shuffle) {
            customersPerDepot.values().forEach(Collections::shuffle);
        }
    }

    Map<Integer, List<Integer>> getGenes() {
        return Collections.unmodifiableMap(genes);
    }

    void setFeasible(boolean feasible) {
        this.feasible = feasible;
    }

    boolean getFeasible() {
        assert scheduled;
        return feasible;
    }

    Map<Integer, List<List<Integer>>> getSchedule(MDVRP problem)  {
        if (!scheduled) {
            schedule = RouteScheduler.scheduleRoutes(this, problem);
            scheduled = true;
        }
        return Collections.unmodifiableMap(schedule);
    }

    public float fitness() {
        return fitness(PROBLEM);
    }

    private float fitness(MDVRP problem) {

        getSchedule(problem);

        float fitness = 0;
        // loop over depots - routes - customers and add distance
        for (var routesPerDepot : schedule.entrySet()) {
            Customer depot = problem.getDepots().get(routesPerDepot.getKey());
            for (List<Integer> route : routesPerDepot.getValue()) {
                Customer position = depot; // start route add depo
                for (Customer base : route.stream().map(problem.getCustomers()::get).collect(Collectors.toList())) {
                    fitness += Util.euclid(position, base);
                    position = base;
                }
                fitness += Util.euclid(position, depot); // end route at depot
            }
        }

        // add feasibility fee
        // TODO: think about "you get what you ask for" - feasibility is more complex than just the flag we have now.
        if (!this.getFeasible()) {
            fitness += UNFEASABILITY_FEE;
        }
        return fitness;
    }

}