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
        //TODO next: append lists in schedule to get Chromosome
        ChromosomeMDVRP daughter = new ChromosomeMDVRP(daughterSchedule, false);

        Map<Integer, List<List<Integer>>> son = Util.deepCopySchedule(dSchedule);
        son.put(depotId, sonDepotRoutes);


        List<ChromosomeMDVRP> offspring = new ArrayList<>();
        offspring.add(son);
        offspring.add(daughter);
        return new ArrayList() ;



    }

    private List<List<Integer>> reinsert(List<List<Integer>> depotSchedule, List<Integer> toReinsert) {

    }
}
