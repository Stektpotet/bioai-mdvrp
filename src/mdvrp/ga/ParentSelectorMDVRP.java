package mdvrp.ga;

import ga.data.Population;
import ga.selection.ParentSelector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ParentSelectorMDVRP implements ParentSelector<ChromosomeMDVRP> {

    private double selectionPressure = 0.8;
    private int poolSize;
    private int numParents;

    public ParentSelectorMDVRP(int poolSize, int numParents, double selectionPressure) {
        this.selectionPressure = selectionPressure;
        this.poolSize = poolSize;
        this.numParents = numParents;
    }
    @Override
    public List<ChromosomeMDVRP> select(Population<ChromosomeMDVRP> population) {
        assert poolSize != 0;

        List<ChromosomeMDVRP> individuals = population.getIndividuals();
        List<ChromosomeMDVRP> parents = new ArrayList<>(poolSize);

        Comparator<ChromosomeMDVRP> fitnessComp = UtilChromosomeMDVRP.chromosomeFitnessComparator();
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
