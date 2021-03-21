package ga.selection;
import ga.data.Chromosome;
import ga.data.Population;

import java.util.List;

public interface ParentSelector<ProblemT, C extends Chromosome<ProblemT>> {
    public List<C> select(Population<ProblemT, C> population);
}
