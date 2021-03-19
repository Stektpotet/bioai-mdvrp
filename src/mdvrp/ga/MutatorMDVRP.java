package mdvrp.ga;

import ga.change.Mutator;
import mdvrp.Depot;
import mdvrp.MDVRP;

public class MutatorMDVRP implements Mutator<ChromosomeMDVRP> {
    private final MDVRP problem;
    private final float pReversal;
    private final float pReroute;
    private final float pSwapping;
    private final float pInter;

    MutatorMDVRP(MDVRP problem, float pReversal, float pReroute, float pSwapping, float pInter) {
        this.problem = problem;
        this.pReversal = pReversal;
        this.pReroute = pReroute;
        this.pSwapping = pSwapping;
        this.pInter = pInter;
    }

    @Override
    public ChromosomeMDVRP mutate(ChromosomeMDVRP chromosome) {

        // 0. Choose depot
        Depot depot = Util.randomChoice(problem.getDepots());
        // 1. Intra-depot mutations
        intraReversal(depot, chromosome);
        intraReroute(depot, chromosome);
        intraSwapping(depot, chromosome);

        // 2. inter-depot mutation
        interDepotSwapping(chromosome);
        return chromosome;
    }

    /** TODO: make a randomChoice that accepts a Map<Integer, T>
     *        finish mutate
     *  TODO: Implement intraReversal
     *  TODO: Implement intraReroute
     *        -> Use recombinator.reinsert (the beautiful code)
     *        -> ChromosomeMDVRPUtil move common functionality from recombinator and this into this Util
     *        -> Move some other things
     *  TODO: Implement intraSwapping
     *  TODO: Implement interDepotSwapping
     *        -> Find a goooder name for this
     *
    **/

    private void intraReversal(Depot depot, ChromosomeMDVRP chromosome) {

    }

    private void intraReroute(Depot depot, ChromosomeMDVRP chromosome) {

    }

    private void intraSwapping(Depot depot, ChromosomeMDVRP chromosome) {

    }
    private void interDepotSwapping(ChromosomeMDVRP chromosome) {

    }
}
