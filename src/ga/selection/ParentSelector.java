package ga.selection;
import ga.data.Chromosome;
import ga.data.Population;

import java.util.List;

public interface ParentSelector<C extends Chromosome> {
    public List<C> select(Population<C> population);
}
