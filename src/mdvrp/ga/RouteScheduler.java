package mdvrp.ga;

import mdvrp.MDVRP;

import java.util.ArrayList;
import java.util.List;

// Chromosome Decoder
public class RouteScheduler {

    public static List<List<List<Integer>>> scheduleRoutes(Chromosome chromosome, MDVRP problem) {
        List<List<List<Integer>>> routes = new ArrayList<>();

        for (List<Integer> gene: chromosome.getGeneStrings()) {
            // TODO: make gene a map and pass kex instead of zero
            List<List<Integer>> trivialSchedule = trivialPhase(0, gene, problem);
        }
        // "copy" outer list - genes
        // construct (trivial, shifting) - routes
        // Ids - bases

        // 1. Trivial part
        // 2. Shifting
    }


    /**
     * @param gene List of customers to be served from one depot
     * @return List of lists (representing routes, containing customerID)
     */
    private static List<List<Integer>> trivialPhase(int depotID, List<Integer> gene, MDVRP problem) {
        List<List<Integer>> schedule = new ArrayList<>();
        int maxVehicleLoad = problem.getDepots().get(depotID).getMaxVehicleLoad();

        while (schedule.size() < problem.getNumMaxVehicles()) {
            List<Integer> route = new ArrayList<>();

            int load = 0;

        }
    }
}
