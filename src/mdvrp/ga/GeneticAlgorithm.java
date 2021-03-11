package mdvrp.ga;

import mdvrp.Customer;
import mdvrp.MDVRP;

import java.util.*;
import java.util.stream.Collectors;

public class GeneticAlgorithm {

    public static Random rand = new Random(69);
    private MDVRP problem;
    private float swappingDistance;
    private float unfeasibilityFee;

    public GeneticAlgorithm(MDVRP problem, float swappingDistance, float unfeasibilityFee) {
        this.problem = problem;
        this.swappingDistance = swappingDistance;
        this.unfeasibilityFee = unfeasibilityFee;
    }

    private Map<Integer, List<List<Integer>>> schedule(Chromosome chromosome)  {
        return RouteScheduler.scheduleRoutes(chromosome, problem);
    }

    private float fitness(Chromosome chromosome) {
        Map<Integer, List<List<Integer>>> schedule = schedule(chromosome);
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
        if (!chromosome.getFeasible()) {
            fitness += unfeasibilityFee;
        }

        return fitness;
    }


}
