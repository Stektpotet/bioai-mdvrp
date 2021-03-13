package mdvrp.ga;

import ga.data.Population;
import ga.selection.ParentSelector;

public class ParentSelectorMDVRP implements ParentSelector<Chromosome> {
    @Override
    public Chromosome[] select(Population<Chromosome> population) {
        return new Chromosome[0];
    }
}
