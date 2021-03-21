package mdvrp.ga;

import mdvrp.Customer;
import mdvrp.Depot;
import mdvrp.MDVRP;
import mdvrp.structures.CustomerSequence;
import mdvrp.structures.Schedule;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


// Chromosome Decoder
//Klara: I suggest putting scheduling functionality in Chromosome, and other utility in UtilChromosomeMDVRP,
//       like that we don't have the sketchyness of a package private feasibility-flag-setter.
public class RouteScheduler {

    private static boolean scheduleFeasible;

    public static Map<Integer, Schedule>  scheduleRoutes(ChromosomeMDVRP chromosome, MDVRP problem) {
        Map<Integer, Schedule> routesPerDepot = new HashMap<>();

        boolean solutionFeasible = true;
        Map<Integer, Depot> depots = problem.getDepots();
        Map<Integer, Customer> customers = problem.getCustomers();
        for (Map.Entry<Integer, CustomerSequence> gene : chromosome.getGenes().entrySet()) {

            Depot depot = depots.get(gene.getKey());
            int maxVehicleLoad = depot.getMaxVehicleLoad();
            double maxVehicleDuration = depot.getMaxDuration();
            int numMaxVehicles = problem.getNumMaxVehicles();

            // TODO: Use stream mappings BEWARE STREAMS ARE CONSUMED!
            // gene.getValue().stream().map(customers::get)

            // TODO: Debug if the boolean type actually gets changed!!
            //      Klara: it doesn't. Very very very bad quickfix (please noooooooooo), but for now: static flag in class.
            scheduleFeasible = true;
            Schedule schedule = trivialPhase(depot, gene.getValue(), customers, maxVehicleLoad, maxVehicleDuration,
                    numMaxVehicles);
            if (!scheduleFeasible)
                System.out.println("Unfeasible Solution Made!");
            // REMINDER: shiftSchedule will not work properly if given an infeasible schedule, see REMINDER in method!
            if (scheduleFeasible) {
                shiftSchedule(schedule, customers, depot, maxVehicleLoad);
            }

            solutionFeasible &= scheduleFeasible;
            routesPerDepot.put(depot.getId(), schedule);
        }
        chromosome.setFeasible(solutionFeasible);
        return Collections.unmodifiableMap(routesPerDepot);
    }



    public static boolean isInsertionDemandFeasible(Stream<Customer> route, Customer insertion, Depot depot) {
        return routeDemandSum(route) + insertion.getDemand() <= depot.getMaxVehicleLoad();
    }
    public static int routeDemandSum(Stream<Customer> customerStream) {
        return customerStream.mapToInt(Customer::getDemand).sum();
    }

    public static int depotScheduleSum(List<List<Integer>> depotSchedule, Map<Integer, Customer> customers) {
        return depotSchedule.stream().map(customers::get).mapToInt(Customer::getDemand).sum();
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
    private static boolean isShiftedShorter(Customer a, Customer shifting, Customer c, Customer depot) {
        double current = Util.duration(a, shifting) + Util.duration(depot, c);
        double shifted = Util.duration(a, depot) + Util.duration(shifting, c);
        return current > shifted;
    }

    /**
     *  Evaluates whether or not the 'route' can accommodate 'customerToShift' at the beginning of the route without
     *  surpassing the maximum vehicle load.
     * @param customerToShift The customer node to be shifted into 'route'.
     * @param customerStream The route to consider shift feasibility on.
     * @param maxVehicleLoad
     * @return
     */
    private static boolean isShiftFeasible(Customer customerToShift, Stream<Customer> customerStream, int maxVehicleLoad) {
        int load = routeDemandSum(customerStream);
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
    //TODO. stream customers from schedule
    private static void shiftSchedule(Schedule schedule, Map<Integer, Customer> customers,
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
     * Distributes customers into routes by iteratively adding customers from gene string till
     * the summed demand surpasses the vehicle capacity.
     * @param geneString List of customers to be served from one depot.
     * @param customers Map of customers. ID -> Customer.
     * @param maxVehicleLoad The maximum load a vehicle can serve in its route.
     * @param numMaxVehicles The maximum number of vehicles a depot can send out.
     * @return List of lists (representing routes, containing customerID)
     */
    //TODO: Use CustomerSequence's customerStream-func
    private static Schedule trivialPhase(Depot depot, CustomerSequence geneString, Map<Integer, Customer> customers,
                                         int maxVehicleLoad, double maxVehicleDuration, int numMaxVehicles) {
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

        if (currentBase < geneString.size()) {
            scheduleFeasible = false;
            var route = schedule.get(schedule.size() - 1);
            for (; currentBase < geneString.size(); currentBase++) {
                Integer customerID = geneString.get(currentBase);
                route.add(customerID);
            }
        }
        return schedule;
    }


//    /**
//     *  In-place shifts the last customer of a route to the beginning of the following route
//     *  if it overall results in feasible and shorter routes.
//     *
//     *  Different from {@see #shiftSchedule} in that it circularly shifts the customers.
//     *
//     * @param schedule The list of routes for the 'depot' to shift.
//     * @param customers Map of customers. ID -> Customer.
//     * @param depot The depot with the routes in question.
//     * @param maxVehicleLoad The maximum load a vehicle can serve in its route.
//     */
//    private static void shiftScheduleRepeated(List<List<Integer>> schedule, Map<Integer, Customer> customers,
//                                      Depot depot, int maxVehicleLoad) {
//        boolean changed = true;
//        while (changed) {
//            changed = false;
//            for (int i = 0; i < schedule.size(); i++) {
//                List<Integer> route = schedule.get(i); // TODO: mini-optimization possible
//                List<Integer> nextRoute = schedule.get((i + 1) % schedule.size());
//
//                if (route.size() == 0)
//                    continue;
//
//                Customer a = route.size() == 1 ? depot : customers.get(route.get(route.size() - 2));
//                Customer b = customers.get(route.get(route.size() - 1));
//                Customer c = nextRoute.size() == 0 ? depot : customers.get(nextRoute.get(0));
//
//                if (isShiftedShorter(a, b, c, depot) && isShiftFeasible(b, nextRoute, customers, maxVehicleLoad)) {
//                    nextRoute.add(0, route.remove(route.size() - 1));
//                    changed = true;
//                }
//            }
//        }
//    }
    static double getScheduleDuration(Depot depot, Stream<Stream<Customer>> customerStreams) {
        return customerStreams.mapToDouble(routeStream -> getRouteDuration(depot, routeStream)).sum();
    }

    public static double getRouteDuration(Depot depot, Stream<Customer> route) {
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
}
