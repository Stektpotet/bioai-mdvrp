package mdvrp.ga;

import ga.data.Chromosome;
import mdvrp.Customer;
import mdvrp.MDVRP;
import mdvrp.MDVRPFiles;
import mdvrp.collections.CustomerSequence;
import mdvrp.collections.Schedule;

import java.util.*;
import java.util.stream.Collectors;

public class ChromosomeMDVRP implements Chromosome {
    // TODO: PLEASE GOD NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO

    public static final MDVRP PROBLEM = MDVRPFiles.ReadFile("res/problems/p01");

    private boolean feasible = true;
    private Map<Integer, CustomerSequence> genes;
    private boolean scheduled = false;
    private Map<Integer, Schedule> solution;
    private static final float UNFEASABILITY_FEE = 1000;

    ChromosomeMDVRP(Map<Integer, CustomerSequence> customersPerDepot, boolean shuffle) {
        // https://stackoverflow.com/questions/8559092/create-an-array-of-arraylists
        genes = customersPerDepot;
        if (shuffle) {
            customersPerDepot.values().forEach(Collections::shuffle);
        }
    }

    ChromosomeMDVRP(Map<Integer, Schedule> solution) {
        genes = new HashMap<>(solution.size());
        for (Map.Entry<Integer, Schedule> entry: solution.entrySet()) {
            Integer depotId = entry.getKey();
            CustomerSequence geneString = entry.getValue().underlyingGeneString();
            genes.put(depotId, geneString);
        }
        scheduled = false;

    }

    Map<Integer, CustomerSequence> getGenes() {
        return Collections.unmodifiableMap(genes);
    }

    void setFeasible(boolean feasible) {
        this.feasible = feasible;
    }

    boolean getFeasible() {
        assert scheduled;
        return feasible;
    }

    Map<Integer, Schedule> getSchedule(MDVRP problem)  {
        if (!scheduled) {
            solution = RouteScheduler.scheduleRoutes(this, problem);
            scheduled = true;
        }
        return Collections.unmodifiableMap(solution);
    }

    public float fitness() {
        return fitness(PROBLEM);
    }

    private float fitness(MDVRP problem) {

        getSchedule(problem);

        float fitness = 0;
        // loop over depots - routes - customers and add distance
        for (var routesPerDepot : solution.entrySet()) {
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