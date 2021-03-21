package mdvrp.ga;

import ga.change.Recombinator;
import mdvrp.Depot;
import mdvrp.MDVRP;
import mdvrp.structures.CustomerSequence;
import mdvrp.structures.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecombinatorMDVRP implements Recombinator<ChromosomeMDVRP> {

    private final MDVRP problem;
    private final double pCrossover;

    public RecombinatorMDVRP(MDVRP problem, double pCrossover) {
        this.problem = problem;
        this.pCrossover = pCrossover;
    }

    @Override
    public List<ChromosomeMDVRP> recombine(final List<ChromosomeMDVRP> parents) {
        int numOffspring = parents.size();
        var parentsIter = parents.iterator();
        List<ChromosomeMDVRP> offspring = new ArrayList<>(numOffspring);
        for (int i = 0; i < Math.floorDiv(numOffspring, 2); i++) {
            ChromosomeMDVRP mum = parentsIter.next();
            ChromosomeMDVRP dad = parentsIter.next();
            if (Util.random.nextFloat() < pCrossover) {
                offspring.addAll(crossover(mum, dad));
            } else {
                offspring.add(mum);
                offspring.add(dad);
            }
        }
        return offspring;
    }

    @Override
    public List<ChromosomeMDVRP> crossover(final ChromosomeMDVRP mum, final ChromosomeMDVRP dad) {
        Map<Integer, Schedule> mSolution = mum.getSolution(problem);
        Map<Integer, Schedule> dSolution = dad.getSolution(problem);

        // choose depot (same for both)
        Integer depotId = Util.randomChoice(new ArrayList<>(mSolution.keySet()));
        Depot depot = problem.getDepots().get(depotId);
        Schedule mDepotRoutes = mSolution.get(depotId);
        Schedule dDepotRoutes = dSolution.get(depotId);

        // choose route (separately for both)
        CustomerSequence mSelectedRoute = Util.randomChoice(mDepotRoutes);
        CustomerSequence dSelectedRoute = Util.randomChoice(dDepotRoutes);

        // remove and reinsert customers from selected route in the opposite schedule
        Schedule dauDepotRoutes = UtilChromosomeMDVRP.removeAndReinsertAll(problem, depot, mDepotRoutes, dSelectedRoute, 0.8);
        Schedule sonDepotRoutes = UtilChromosomeMDVRP.removeAndReinsertAll(problem, depot, dDepotRoutes, mSelectedRoute, 0.8);

        // make new Chromosomes
        Map<Integer, Schedule> daughterSchedule = UtilChromosomeMDVRP.deepCopySolution(mSolution);
        daughterSchedule.put(depotId, dauDepotRoutes);
        ChromosomeMDVRP daughter = new ChromosomeMDVRP(daughterSchedule);

        Map<Integer, Schedule> sonSchedule = UtilChromosomeMDVRP.deepCopySolution(dSolution);
        sonSchedule.put(depotId, sonDepotRoutes);
        ChromosomeMDVRP son = new ChromosomeMDVRP(sonSchedule);


        List<ChromosomeMDVRP> offspring = new ArrayList<>();
        offspring.add(son);
        offspring.add(daughter);
        return offspring;

    }
}
