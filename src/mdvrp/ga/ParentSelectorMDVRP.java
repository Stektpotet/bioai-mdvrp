package mdvrp.ga;

import ga.data.Population;
import ga.selection.ParentSelector;
import mdvrp.MDVRP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ParentSelectorMDVRP implements ParentSelector<MDVRP, ChromosomeMDVRP> {

    private final MDVRP problem;
    private final double selectionPressure;
    private final int poolSize;
    private final int numParents;

    public ParentSelectorMDVRP(MDVRP problem, int poolSize, int numParents, double selectionPressure) {
        this.selectionPressure = selectionPressure;
        this.poolSize = poolSize;
        this.numParents = numParents;
        this.problem = problem;
    }
    @Override
    public List<ChromosomeMDVRP> select(Population<MDVRP, ChromosomeMDVRP> population) {
        assert poolSize != 0;

        List<ChromosomeMDVRP> individuals = population.getIndividuals();
        List<ChromosomeMDVRP> parents = new ArrayList<>(poolSize);

        Comparator<ChromosomeMDVRP> fitnessComp = UtilChromosomeMDVRP.chromosomeFitnessComparator(problem);
        for (int i = 0; i < numParents; i++) {
            List<ChromosomeMDVRP> pool = Util.randomChoice(individuals, poolSize, false);
            if (Util.random.nextFloat() < selectionPressure) {
                parents.add(Collections.min(pool, fitnessComp));
            } else {
                parents.add(Util.randomChoice(pool));
            }
        }
        return parents;
    }
}
