package mdvrp.ga;

import ga.change.Recombinator;
import ga.data.Chromosome;
import mdvrp.MDVRP;

import java.util.ArrayList;
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
        Map<Integer, List<List<Integer>>> mSchedule = mum.getSchedule(problem);
        Map<Integer, List<List<Integer>>> dSchedule = dad.getSchedule(problem);

        // choose depot (same for both)
        Integer depotId = Util.random.nextInt(mSchedule.size());
        List<List<Integer>> mDepotRoutes = mSchedule.get(depotId);
        List<List<Integer>> dDepotRoutes = dSchedule.get(depotId);

        // choose route (separately for both)
        List<Integer> mSelectedRoute = mDepotRoutes.get(Util.random.nextInt(mDepotRoutes.size()));
        List<Integer> dSelectedRoute = dDepotRoutes.get(Util.random.nextInt(dDepotRoutes.size()));

        // remove and reinsert customers from selected route in the opposite schedule
        List<List<Integer>> dauDepotRoutes = reinsert(mDepotRoutes, dSelectedRoute);
        List<List<Integer>> sonDepotRoutes = reinsert(dDepotRoutes, mSelectedRoute);

        // make new Chromosomes
        Map<Integer, List<List<Integer>>> daughterSchedule = Util.deepCopySchedule(mSchedule);
        daughterSchedule.put(depotId, dauDepotRoutes);
        ChromosomeMDVRP daughter = new ChromosomeMDVRP(daughterSchedule);

        Map<Integer, List<List<Integer>>> sonSchedule = Util.deepCopySchedule(dSchedule);
        sonSchedule.put(depotId, sonDepotRoutes);
        ChromosomeMDVRP son = new ChromosomeMDVRP(sonSchedule);


        List<ChromosomeMDVRP> offspring = new ArrayList<>();
        offspring.add(son);
        offspring.add(daughter);
        return offspring;



    }

    private List<List<Integer>> reinsert(List<List<Integer>> depotSchedule, List<Integer> toReinsert) {
        for (var customerId : toReinsert) {
            if (!depotSchedule.remove(customerId)) {
                continue;
            }

            for (var route : depotSchedule) {
                for (int i = 0; i < route.size() + 1; i++) {
                    RouteScheduler.geneFeasibilitz(depotSchedule)
                    //          if feasible
                    //              compute insertion cost
                    //              store in feasibleLocations

                }
            }
        }

        // for each customer in to Reinsert:
        //      take customer out of depotSchedule

        //      for each locations in depotSchedule:
        //      if feasibleLocations is empty:
        //          TODO tbd
        //      if random r < 0.8
        //          choose best location in feasibleLocations
        //      else
        //          choose random feasibleLocations

        return null;
    }
}
