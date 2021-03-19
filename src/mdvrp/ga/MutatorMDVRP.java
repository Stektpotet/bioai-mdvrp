package mdvrp.ga;

import ga.change.Mutator;
import ga.data.Chromosome;
import ga.data.Population;
import mdvrp.Depot;
import mdvrp.MDVRP;
import mdvrp.structures.CustomerSequence;
import mdvrp.structures.Schedule;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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


        if (pInter > Util.random.nextFloat()) {
            // 2. inter-depot mutation
            chromosome = interDepotSwapping(chromosome);
        } else {
            // 1. Intra-depot mutations
            chromosome = intraReversal(depot, chromosome);
            chromosome = intraReroute(depot, chromosome);
            chromosome = intraSwapping(depot, chromosome);
        }

        return chromosome;
    }

    /** TODO: Make the toString function work again
     *
     *  TODO: make a randomChoice that accepts a Map<Integer, T>
     *        finish mutate  [HALVOR]
     *  TODO: Implement intraReversal [KLARA]
     *  TODO: Implement intraReroute [KLARA]
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
        Map<Integer, CustomerSequence> genes = UtilChromosomeMDVRP.deepCopyGenes(chromosome);
        genes.put(depot.getId(), depotGeneString);
        return new ChromosomeMDVRP(genes, false);
    }

    public ChromosomeMDVRP intraReroute(Depot depot, ChromosomeMDVRP chromosome) {

        // check if mutation applicable
        if (Util.random.nextFloat() > pReroute) {
            return chromosome;
        }

        Map<Integer, Schedule> solution = chromosome.getSolution(problem);
        Schedule depotSchedule = solution.get(depot.getId());

        // choose customer to be rerouted
        Integer customerId = Util.randomChoice(depotSchedule.underlyingGeneString());
        CustomerSequence toBeReinserted = new CustomerSequence();
        toBeReinserted.add(customerId);

        // reroute
        Schedule mutatedSchedule = UtilChromosomeMDVRP.reinsert(problem, depot, depotSchedule, toBeReinserted, 1);

        // make and return new Chromosome with mutation
        Map<Integer, Schedule> solutionCopy = UtilChromosomeMDVRP.deepCopySolution(solution);
       solutionCopy.put(depot.getId(), mutatedSchedule);
       return new ChromosomeMDVRP(solutionCopy);
    }

    private ChromosomeMDVRP intraSwapping(Depot depot, ChromosomeMDVRP chromosome) {
        if (Util.random.nextFloat() > pSwapping)
            return chromosome;

        var soultion = UtilChromosomeMDVRP.deepCopySolution(chromosome.getSolution(problem));
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

    private ChromosomeMDVRP interDepotSwapping(Map<Integer, List<Integer>> swapMap, ChromosomeMDVRP chromosome) {
        return null;
    }
}
