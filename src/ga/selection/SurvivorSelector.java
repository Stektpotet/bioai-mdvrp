package ga.selection;

import ga.data.Chromosome;
import ga.data.Population;

import java.util.List;

public interface SurvivorSelector<ProblemT, Pop extends Population<ProblemT, C>, C extends Chromosome<ProblemT>> {
    public Pop select(Pop generation, List<C> parents, List<C> offspring);
}
