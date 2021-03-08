package mdvrp.ga;

import mdvrp.Customer;
import mdvrp.Depot;
import mdvrp.MDVRP;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


// Chromosome Decoder
public class RouteScheduler {

    public static Map<Integer, List<List<Integer>>>  scheduleRoutes(Chromosome chromosome, MDVRP problem) {
        Map<Integer, List<List<Integer>>> routesPerDepot = new HashMap<>();

        boolean feasible = true;
        Map<Integer, Depot> depots = problem.getDepots();
        Map<Integer, Customer> customers = problem.getCustomers();
        for (Map.Entry<Integer, List<Integer>> gene : chromosome.getGenes().entrySet()) {

            Depot depot = depots.get(gene.getKey());
            int maxVehicleLoad = depot.getMaxVehicleLoad();
            int numMaxVehicles = problem.getNumMaxVehicles();

            // TODO: Use stream mappings BEWARE STREAMS ARE CONSUMED!
            // gene.getValue().stream().map(customers::get)

            List<List<Integer>> schedule = trivialPhase(gene.getValue(), customers, maxVehicleLoad, numMaxVehicles);
            shiftSchedule(schedule, customers, depot, maxVehicleLoad);

            Stream<Stream<Customer>> routeStream = schedule.stream().map(a -> a.stream().map(customers::get));
            feasible &= geneFeasibility(routeStream, maxVehicleLoad);
            routesPerDepot.put(depot.getId(), schedule);
        }
        chromosome.setFeasible(feasible);
        return routesPerDepot;
    }


    private static boolean geneFeasibility(Stream<Stream<Customer>> routes, int maxVehicleLoad) {
        return routes.allMatch(routeStream -> maxVehicleLoad >= routeStream.mapToInt(Customer::getDemand).sum());
    }

    /**
     *  Evaluates whether or not a shift results in shorter routes.
     *  ... -> a -> b -> depot -> c -> ...   vs   ... -> a -> depot -> b -> c -> ...
     *
     * @param a The next to last node in the route to shift from.
     * @param b The node to shift to the next route.
     * @param c The first node in the next route.
     * @param depot The depot the routes connect to.
     * @return true if shifting 'b' to the next route results in shorter routes, false otherwise.
     */
    private static boolean isShiftedShorter(Customer a, Customer b, Customer c, Customer depot) {
        return Util.euclid(a, b) + Util.euclid(depot, c) > Util.euclid(a, depot) + Util.euclid(b, c);
    }

    /**
     *  Evaluates whether or not the 'route' can accommodate 'customerToShift' at the beginning of the route without
     *  surpassing the maximum vehicle load.
     * @param customerToShift The customer node to be shifted into 'route'.
     * @param route The route to consider shift feasibility on.
     * @param customers The customer map,
     * @param maxVehicleLoad
     * @return
     */
    private static boolean isShiftFeasible(Customer customerToShift, List<Integer> route, Map<Integer, Customer> customers, int maxVehicleLoad) {
        int load = 0;
        for (Integer customerID : route) {
//            route.stream().map(customers::get).mapToInt(Customer::getDemand).sum();
            load += customers.get(customerID).getDemand();
        }
        load += customerToShift.getDemand();
        return load <= maxVehicleLoad;
    }
    /**
     *  In-place shifts the last customer of a route to the beginning of the following route
     *  if it overall results in feasible and shorter routes.
     * @param schedule The list of routes for the 'depot' to shift.
     * @param customers Map of customers. ID -> Customer.
     * @param depot The depot with the routes in question.
     * @param maxVehicleLoad The maximum load a vehicle can serve in its route.
     */
    private static void shiftSchedule(List<List<Integer>> schedule, Map<Integer, Customer> customers,
                                                     Depot depot, int maxVehicleLoad) {
        boolean changed = true;
        while (changed) {
            changed = false;
            var route = schedule.get(0);
            for (int i = 1; i < schedule.size()-1; i++) {
                List<Integer> nextRoute = schedule.get(i);
                if (route.size() == 0) {
                    route = nextRoute;
                    continue;
                }
                Customer a = route.size() == 1 ? depot : customers.get(route.get(route.size() - 2));
                Customer b = customers.get(route.get(route.size() - 1));
                Customer c = nextRoute.size() == 0 ? depot : customers.get(nextRoute.get(0));
                if (isShiftedShorter(a, b, c, depot) && isShiftFeasible(b, nextRoute, customers, maxVehicleLoad)) {
                    nextRoute.add(0, route.remove(route.size() - 1));
                    changed = true;
                }

                route = nextRoute;
            }
        }

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
    private static List<List<Integer>> trivialPhase(List<Integer> geneString, Map<Integer, Customer> customers,
                                                    int maxVehicleLoad, int numMaxVehicles) {
        List<List<Integer>> schedule = new ArrayList<>();

        int currentBase = 0;
        while (schedule.size() < numMaxVehicles) {
            List<Integer> route = new ArrayList<>();

            int load = 0;
            for (; currentBase < geneString.size(); currentBase++) {
                Integer customerID = geneString.get(currentBase);
                load += customers.get(customerID).getDemand();

                if (load > maxVehicleLoad) {
                    break;
                }

                route.add(customerID);
            }
            schedule.add(route);
        }

        if (currentBase < geneString.size()) {
//
            var route = schedule.get(schedule.size() - 1);
            for (; currentBase < geneString.size(); currentBase++) {
                Integer customerID = geneString.get(currentBase);
                route.add(customerID);
            }
        }
        return schedule;
    }


    /**
     *  In-place shifts the last customer of a route to the beginning of the following route
     *  if it overall results in feasible and shorter routes.
     *
     *  Different from {@see #shiftSchedule} in that it circularly shifts the customers.
     *
     * @param schedule The list of routes for the 'depot' to shift.
     * @param customers Map of customers. ID -> Customer.
     * @param depot The depot with the routes in question.
     * @param maxVehicleLoad The maximum load a vehicle can serve in its route.
     */
    private static void shiftScheduleRepeated(List<List<Integer>> schedule, Map<Integer, Customer> customers,
                                      Depot depot, int maxVehicleLoad) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < schedule.size(); i++) {
                List<Integer> route = schedule.get(i); // TODO: mini-optimization possible
                List<Integer> nextRoute = schedule.get((i + 1) % schedule.size());

                if (route.size() == 0)
                    continue;

                Customer a = route.size() == 1 ? depot : customers.get(route.get(route.size() - 2));
                Customer b = customers.get(route.get(route.size() - 1));
                Customer c = nextRoute.size() == 0 ? depot : customers.get(nextRoute.get(0));

                if (isShiftedShorter(a, b, c, depot) && isShiftFeasible(b, nextRoute, customers, maxVehicleLoad)) {
                    nextRoute.add(0, route.remove(route.size() - 1));
                    changed = true;
                }
            }
        }
    }

}
