package mdvrp.ga;

import mdvrp.Customer;
import mdvrp.Depot;
import mdvrp.MDVRP;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



// Chromosome Decoder
public class RouteScheduler {

    public static List<List<List<Integer>>> scheduleRoutes(Chromosome chromosome, MDVRP problem) throws Exception {
        List<List<List<Integer>>> routesPerDepot = new ArrayList<>();

        List<Depot> depots = problem.getDepots();
        List<Customer> customers = problem.getCustomers();
        for (Map.Entry<Integer, List<Integer>> gene : chromosome.getGenes().entrySet()) {

            Depot depot = depots.get(gene.getKey());
            int maxVehicleLoad = depot.getMaxVehicleLoad();

            List<List<Integer>> schedule = trivialPhase(gene.getValue(), customers, maxVehicleLoad, problem.getNumMaxVehicles());
            shiftSchedule(schedule, customers, depot, maxVehicleLoad);

            routesPerDepot.add(schedule);
        }

        return routesPerDepot;
    }

    private static boolean isShiftGood(Customer a, Customer b, Customer c, Customer depot) {
        return Util.euclid(a, b) + Util.euclid(depot, c) > Util.euclid(a, depot) + Util.euclid(b, c);
    }

    private static boolean isShiftFeasible(Customer customerToShift, List<Integer> route, List<Customer> customers, int maxVehicleLoad) {
        int load = 0;
        for (Integer customerID : route) {
            load += customers.get(customerID).getDemand();
        }
        load += customerToShift.getDemand();
        return load <= maxVehicleLoad;
    }

    private static void shiftSchedule(List<List<Integer>> schedule, List<Customer> customers,
                                                     Depot depot, int maxVehicleLoad) {
        // TODO: Feasibility
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < schedule.size()-1; i++) {
                List<Integer> route = schedule.get(i); // TODO: mini-optimization possible
                List<Integer> nextRoute = schedule.get(i + 1);
                if (route.size() == 0)
                    continue;

                Customer a = route.size() == 1 ? depot : customers.get(route.get(route.size() - 2));
                Customer b = customers.get(route.get(route.size() - 1));
                Customer c = nextRoute.size() == 0 ? depot : customers.get(nextRoute.get(0));

                if (isShiftGood(a, b, c, depot) && isShiftFeasible(b, nextRoute, customers, maxVehicleLoad)) {
                    schedule.get(i + 1).add(0, route.remove(route.size() - 1));
                    changed = true;
                }
            }
        }
    }

    /**
     * @param geneString List of customers to be served from one depot
     * @param customers
     * @param maxVehicleLoad
     * @param numMaxVehicles
     * @return List of lists (representing routes, containing customerID)
     */
    private static List<List<Integer>> trivialPhase(List<Integer> geneString, List<Customer> customers,
                                                    int maxVehicleLoad, int numMaxVehicles) throws Exception {
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

        if (currentBase != geneString.size() - 1) {
            throw new Exception("Chromosome cannot be validly scheduled!");
        }
        return schedule;
    }
}
