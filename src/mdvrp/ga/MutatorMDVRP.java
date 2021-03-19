package mdvrp.ga;

import ga.change.Mutator;
import mdvrp.Depot;
import mdvrp.MDVRP;
import mdvrp.structures.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        chromosome = intraSwapping(depot, chromosome);

        // 2. inter-depot mutation
        interDepotSwapping(chromosome);
        return chromosome;
    }

    /** TODO: Make the toString function work again
     *
     *  TODO: make a randomChoice that accepts a Map<Integer, T>
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

    private void intraReversal(Depot depot, ChromosomeMDVRP chromosome) {

    }

    private void intraReroute(Depot depot, ChromosomeMDVRP chromosome) {

    }

    private ChromosomeMDVRP intraSwapping(Depot depot, ChromosomeMDVRP chromosome) {
        if (Util.random.nextFloat() >= pSwapping)
            return chromosome;

        var soultion = Util.deepCopySolution(chromosome.getSolution(problem));
        Schedule schedule = soultion.get(depot.getId());

        List<Integer> nonEmptyRouteIdx = IntStream.rangeClosed(0, schedule.size() - 1).filter(
                i -> !schedule.get(i).isEmpty()
        ).boxed().collect(Collectors.toList());
        // Select two separate routes
        var routeA = schedule.get(Util.randomChoiceRemove(nonEmptyRouteIdx));
        var routeB = schedule.get(Util.randomChoiceRemove(nonEmptyRouteIdx));

        var idxSwapA = Util.random.nextInt(routeA.size());
        var idxSwapB = Util.random.nextInt(routeB.size());
        // DO the swap

        var a = routeA.remove(idxSwapA);
        var b = routeB.remove(idxSwapB);

        routeA.add(idxSwapA, b);
        routeB.add(idxSwapB, a);

        soultion.put(depot.getId(), schedule);

        return new ChromosomeMDVRP(soultion);
    }
    private void interDepotSwapping(ChromosomeMDVRP chromosome) {

    }
}
