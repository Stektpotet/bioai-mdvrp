package mdvrp.ga;

import ga.change.Recombinator;
import ga.data.Chromosome;
import mdvrp.Customer;
import mdvrp.MDVRP;
import mdvrp.collections.CustomerSequence;
import mdvrp.collections.Schedule;

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
        Map<Integer, Schedule> mSchedule = mum.getSchedule(problem);
        Map<Integer, Schedule> dSchedule = dad.getSchedule(problem);

        // choose depot (same for both)
        Integer depotId = Util.random.nextInt(mSchedule.size());
        Schedule mDepotRoutes = mSchedule.get(depotId);
        Schedule dDepotRoutes = dSchedule.get(depotId);

        // choose route (separately for both)
        CustomerSequence mSelectedRoute = mDepotRoutes.get(Util.random.nextInt(mDepotRoutes.size()));
        CustomerSequence dSelectedRoute = dDepotRoutes.get(Util.random.nextInt(dDepotRoutes.size()));

        // remove and reinsert customers from selected route in the opposite schedule
        Schedule dauDepotRoutes = reinsert(mDepotRoutes, dSelectedRoute);
        Schedule sonDepotRoutes = reinsert(dDepotRoutes, mSelectedRoute);

        // make new Chromosomes
        Map<Integer, Schedule> daughterSchedule = Util.deepCopySchedule(mSchedule);
        daughterSchedule.put(depotId, dauDepotRoutes);
        ChromosomeMDVRP daughter = new ChromosomeMDVRP(daughterSchedule);

        Map<Integer, Schedule> sonSchedule = Util.deepCopySchedule(dSchedule);
        sonSchedule.put(depotId, sonDepotRoutes);
        ChromosomeMDVRP son = new ChromosomeMDVRP(sonSchedule);


        List<ChromosomeMDVRP> offspring = new ArrayList<>();
        offspring.add(son);
        offspring.add(daughter);
        return offspring;



    }

    private Schedule reinsert(Schedule depotSchedule, CustomerSequence toReinsert) {

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
