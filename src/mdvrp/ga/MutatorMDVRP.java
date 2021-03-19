package mdvrp.ga;

import ga.change.Mutator;
import mdvrp.Depot;
import mdvrp.MDVRP;
import mdvrp.structures.CustomerSequence;
import mdvrp.structures.Schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MutatorMDVRP implements Mutator<ChromosomeMDVRP> {
    private final MDVRP problem;
    private final float pReversal;
    private final float pReroute;
    private final float pSwapping;
    private final float pInter;

    public MutatorMDVRP(MDVRP problem, float pReversal, float pReroute, float pSwapping, float pInter) {
        this.problem = problem;
        this.pReversal = pReversal;
        this.pReroute = pReroute;
        this.pSwapping = pSwapping;
        this.pInter = pInter;
    }

    @Override
    public ChromosomeMDVRP mutate(ChromosomeMDVRP chromosome) {
        Map<Integer, Depot> depots = problem.getDepots();
        // 0. Choose depot
        Depot depot = Util.randomChoice(new ArrayList<>(depots.values()));

        // 1. Intra-depot mutations
        intraReversal(depot, chromosome);
        intraReroute(depot, chromosome);
        intraSwapping(depot, chromosome);

        // 2. inter-depot mutation
        interDepotSwapping(chromosome);
        return chromosome;
    }

    /** TODO: make a randomChoice that accepts a Map<Integer, T>
     *        finish mutate  [HALVOR]
     *  TODO: Implement intraReversal [KLARA]
     *  TODO: Implement intraReroute
     *        -> Use recombinator.reinsert (the beautiful code)
     *        -> ChromosomeMDVRPUtil move common functionality from recombinator and this into this Util
     *        -> Move some other things
     *  TODO: Implement intraSwapping [HALVOR]
     *  TODO: Implement interDepotSwapping
     *        -> Find a goooder name for this
     *
    **/

    private ChromosomeMDVRP intraReversal(Depot depot, ChromosomeMDVRP chromosome) {

        // check if mutation applicable
        if (Util.random.nextFloat() > pReversal) {
            return chromosome;
        }

        Schedule depotSchedule = chromosome.getSolution(problem).get(depot.getId());
        CustomerSequence depotGeneString = depotSchedule.underlyingGeneString();

        // choose start and stop uniformly at random and reverse Customers in between
        int positionBound = depotGeneString.size() + 1;
        int i = Util.random.nextInt(positionBound);
        int j = Util.random.nextInt(positionBound);
        Collections.reverse(depotGeneString.subList(Math.min(i, j), Math.max(j, i)));

        // make and return new Chromosome with mutation
        Map<Integer, CustomerSequence> genes = chromosome.deepCopyGenes();
        genes.put(depot.getId(), depotGeneString);
        return new ChromosomeMDVRP(genes, false);
    }

    private void intraReroute(Depot depot, ChromosomeMDVRP chromosome) {

    }

    private void intraSwapping(Depot depot, ChromosomeMDVRP chromosome) {

    }
    private void interDepotSwapping(ChromosomeMDVRP chromosome) {

    }
}
