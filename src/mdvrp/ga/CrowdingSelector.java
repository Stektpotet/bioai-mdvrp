package mdvrp.ga;

import ga.selection.SurvivorSelector;
import mdvrp.MDVRP;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CrowdingSelector implements SurvivorSelector<MDVRP, PopulationMDVRP, ChromosomeMDVRP> {

    private final int crowdingFactor;
    private final MDVRP problem;

    public CrowdingSelector(final MDVRP problem, final int crowdingFactor) {
        this.problem = problem;
        this.crowdingFactor = crowdingFactor;
    }
    @Override
    public PopulationMDVRP select(PopulationMDVRP generation, List<ChromosomeMDVRP> parents, List<ChromosomeMDVRP> offspring) {
        for (ChromosomeMDVRP child : offspring) {

            Comparator<ChromosomeMDVRP> differenceComparator =
                    (a, b) -> UtilChromosomeMDVRP.hammingDistance(a, child) - UtilChromosomeMDVRP.hammingDistance(b, child);

            List<ChromosomeMDVRP> individuals = generation.getIndividuals();
            List<ChromosomeMDVRP> comparisonPool = Util.randomChoice(individuals, crowdingFactor, false);
            ChromosomeMDVRP mostSimilarParent = Collections.min(comparisonPool, differenceComparator);
            
            //if (mostSimilarParent.fitness(problem) > child.fitness(problem)) {
                individuals.remove(mostSimilarParent);
                individuals.add(child);
            //}
        }

        return null;
    }
}
