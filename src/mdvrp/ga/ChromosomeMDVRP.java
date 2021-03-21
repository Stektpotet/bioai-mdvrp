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

public class ChromosomeMDVRP implements Chromosome<MDVRP> {

    private final Map<Integer, CustomerSequence> genes;
    private boolean scheduled = false;
    private Map<Integer, Schedule> solution;
    private double unfeasibilityFee = 0;

    // duration: add unfeasible duration to fitness
    // demand: add unfeasible demand to fitness? (eventually scale according to something)
    // fee for overall demand of depot infeasible not depending on order? Or just make mutation and crossover smarter?

    ChromosomeMDVRP(Map<Integer, CustomerSequence> customersPerDepot, boolean shuffle) {
        // https://stackoverflow.com/questions/8559092/create-an-array-of-arraylists
        genes = customersPerDepot;
        if (shuffle) {
            customersPerDepot.values().forEach(geneString -> Collections.shuffle(geneString, Util.random));
        }
    }

    ChromosomeMDVRP(Map<Integer, Schedule> solution) {
        genes = new HashMap<>(solution.size());
        for (Map.Entry<Integer, Schedule> entry: solution.entrySet()) {
            Integer depotId = entry.getKey();
            CustomerSequence geneString = entry.getValue().underlyingGeneString();
            genes.put(depotId, geneString);
        }
    }

    Map<Integer, CustomerSequence> getGenes() {
        return Collections.unmodifiableMap(genes);
    }

    public boolean isFeasible(MDVRP problem) {
        if (!scheduled) {
            solution = schedule(problem);
            scheduled = true;
        }
        return unfeasibilityFee > 0;
    }

    // Klara: why are we able to give the problem here and not elsewhere? That can be uuuuuuuused!
    public Map<Integer, Schedule> getSolution(MDVRP problem)  {
        if (!scheduled) {
            solution = schedule(problem);
            scheduled = true;
        }
        return Collections.unmodifiableMap(solution);
    }

    public double fitness(MDVRP problem) {

        getSolution(problem);

        double fitness = 0;
        // loop over depots - routes - customers and add distance
        for (var routesPerDepot : solution.entrySet()) {
            Customer depot = problem.getDepots().get(routesPerDepot.getKey());
            for (List<Integer> route : routesPerDepot.getValue()) {
                Customer position = depot; // start route at depo
                for (Customer base : route.stream().map(problem.getCustomers()::get).collect(Collectors.toList())) {
                    fitness += Util.duration(position, base);
                    position = base;
                }
                fitness += Util.duration(position, depot); // end route at depot
            }
        }
        return fitness + unfeasibilityFee;
    }

    @Override
    public String toString() {
        return genes.entrySet().stream().map(g -> g.getValue().stream().map(String::valueOf).collect(
                Collectors.joining(" ", String.format("%d ", g.getKey()), "| "))
        ).collect(Collectors.joining());
    }

    public Map<Integer, Schedule> schedule(MDVRP problem) {
        Map<Integer, Schedule> routesPerDepot = new HashMap<>();

        Map<Integer, Depot> depots = problem.getDepots();
        Map<Integer, Customer> customers = problem.getCustomers();
        unfeasibilityFee = 0;
        for (Map.Entry<Integer, CustomerSequence> gene : this.getGenes().entrySet()) {

            Depot depot = depots.get(gene.getKey());
            int maxVehicleLoad = depot.getMaxVehicleLoad();
            double maxVehicleDuration = depot.getMaxDuration();
            int numMaxVehicles = problem.getNumMaxVehicles();

            Schedule schedule = scheduleRoute(depot, gene.getValue(), customers, maxVehicleLoad, maxVehicleDuration,
                    numMaxVehicles);
            routesPerDepot.put(depot.getId(), schedule);
        }
        return Collections.unmodifiableMap(routesPerDepot);
    }

    /**
     * Distributes customers into routes by iteratively adding customers from gene string till
     * the summed demand surpasses the vehicle capacity.
     * @param geneString List of customers to be served from one depot.
     * @param customers Map of customers. ID -> Customer.
     * @param maxVehicleLoad The maximum load a vehicle can serve in its route.
     * @param numMaxVehicles The maximum number of vehicles a depot can send out.
     * @return List of lists (representing routes, containing customerID)
     */
    //TODO: Use CustomerSequence's customerStream-func
    private Schedule scheduleRoute(Depot depot, CustomerSequence geneString, Map<Integer, Customer> customers,
                                   int maxVehicleLoad, double maxVehicleDuration, int numMaxVehicles) {

        if (UtilChromosomeMDVRP.geneStringDemand(geneString, customers) > maxVehicleLoad * numMaxVehicles) {
            // TODO think about reasonable value for this
            unfeasibilityFee += 50;
        }
        Schedule schedule = new Schedule();

        int currentBase = 0;
        while (schedule.size() < numMaxVehicles) {
            CustomerSequence route = new CustomerSequence();

            int load = 0;
            double travelDuration = 0;

            Customer position = depot;

            for (; currentBase < geneString.size(); currentBase++) {
                Integer baseId = geneString.get(currentBase);
                Customer base = customers.get(baseId);

                load += base.getDemand();
                travelDuration += Util.duration(position, base);
                double wayBackDuration = Util.duration(base, depot);
                if (load > maxVehicleLoad || travelDuration + wayBackDuration > maxVehicleDuration) {
                    break;
                }

                position = base;
                route.add(baseId);
            }
            schedule.add(route);
        }

        //TODO make an iterator
        if (currentBase < geneString.size()) {
            CustomerSequence lastRoute = schedule.get(schedule.size() - 1);

            double unfeasibleDistance = 0;
            int unfeasibleDemand = 0;
            Customer position = customers.get(geneString.get(currentBase - 1));
            for (int i = currentBase; i < geneString.size(); i++) {
                Integer customerId = geneString.get(i);
                Customer next = customers.get(customerId);

                // add unfeasible customers to last route
                lastRoute.add(customerId);

                // accumulating distance and demand of infeasible customers
                unfeasibleDistance += Util.duration(position, next);
                unfeasibleDemand += next.getDemand();

                position = next;
            }
            unfeasibleDistance += Util.duration(position, depot);

            //TODO weight demand and distance fee
            unfeasibilityFee += unfeasibleDemand + unfeasibleDistance;
        } else {
            // REMINDER: shiftSchedule will not work properly if given an infeasible schedule, see REMINDER in method!
            shiftSchedule(schedule, customers, depot, maxVehicleLoad);
        }
        return schedule;
    }

    /**
     *  In-place shifts the last customer of a route to the beginning of the following route
     *  if it overall results in feasible and shorter routes.
     * @param schedule The list of routes for the 'depot' to shift.
     * @param customers Map of customers. ID -> Customer.
     * @param depot The depot with the routes in question.
     * @param maxVehicleLoad The maximum load a vehicle can serve in its route.
     */
    //TODO. stream customers from schedule
    private void shiftSchedule(Schedule schedule, Map<Integer, Customer> customers,
                                      Depot depot, int maxVehicleLoad) {
        boolean changed = true;
        while (changed) {
            changed = false;
            var route = schedule.get(0);
            for (int i = 1; i < schedule.size(); i++) {
                CustomerSequence nextRoute = schedule.get(i);
                if (route.size() == 0) {
                    route = nextRoute;
                    continue;
                }
                Customer a = route.size() == 1 ? depot : customers.get(route.get(route.size() - 2));
                Customer b = customers.get(route.get(route.size() - 1));
                Customer c = nextRoute.size() == 0 ? depot : customers.get(nextRoute.get(0));

                // REMINDER: as long as we don't shift infeasible trivial schedules, a shorter route will never break
                //           the maximumDuration constraint.
                if (isShiftedShorter(a, b, c, depot) && isShiftFeasible(b, nextRoute.streamCustomers(customers), maxVehicleLoad)) {
                    nextRoute.add(0, route.remove(route.size() - 1));
                    changed = true;
                }

                route = nextRoute;
            }
        }

    }

    /**
     *  Evaluates whether or not the 'route' can accommodate 'customerToShift' at the beginning of the route without
     *  surpassing the maximum vehicle load.
     * @param customerToShift The customer node to be shifted into 'route'.
     * @param route The route to consider shift feasibility on.
     * @param maxVehicleLoad
     * @return
     */
    private boolean isShiftFeasible(Customer customerToShift, Stream<Customer> route, int maxVehicleLoad) {
        int load = UtilChromosomeMDVRP.routeDemand(route);
        load += customerToShift.getDemand();
        return load <= maxVehicleLoad;
    }

    /**
     *  Evaluates whether or not a shift results in shorter routes.
     *  ... -> a -> shifting -> depot -> c -> ...   vs   ... -> a -> depot -> shifting -> c -> ...
     *
     * @param a The next to last node in the route to shift from.
     * @param shifting The node to shift to the next route.
     * @param c The first node in the next route.
     * @param depot The depot the routes connect to.
     * @return true if shifting 'shifting' to the next route results in shorter routes, false otherwise.
     */
    private boolean isShiftedShorter(Customer a, Customer shifting, Customer c, Customer depot) {
        double current = Util.duration(a, shifting) + Util.duration(depot, c);
        double shifted = Util.duration(a, depot) + Util.duration(shifting, c);
        return current > shifted;
    }
}