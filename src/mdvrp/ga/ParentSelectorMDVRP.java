package mdvrp.ga;

import ga.data.Population;
import ga.selection.ParentSelector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ParentSelectorMDVRP implements ParentSelector<ChromosomeMDVRP> {

    private int poolSize;
    private int numParents;

    public ParentSelectorMDVRP(int poolSize, int numParents) {

        this.poolSize = poolSize;
        this.numParents = numParents;
    }
    @Override
    public List<ChromosomeMDVRP> select(Population<ChromosomeMDVRP> population) {
        assert poolSize != 0;

        List<ChromosomeMDVRP> individuals = population.getIndividuals();
        List<ChromosomeMDVRP> parents = new ArrayList<>(poolSize);

        for (int i = 0; i < numParents; i++) {
            if (Util.random.nextFloat() < 0.8) {
                Comparator<ChromosomeMDVRP> fitnessComp = (a, b) -> (int) Math.signum(a.fitness() - b.fitness());
                parents.add(Util.random.ints(poolSize).mapToObj(individuals::get).min(fitnessComp).get());
            } else {
                parents.add(individuals.get(Util.random.nextInt(individuals.size())));
            }
        }
        return parents;
    }
}
