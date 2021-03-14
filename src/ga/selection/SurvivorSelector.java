package ga.selection;

import ga.data.Chromosome;
import ga.data.Population;

import java.util.List;

public interface SurvivorSelector<Pop extends Population<C>, C extends Chromosome> {
    public Pop select(Pop generation, List<C> parents, List<C> offspring);
}
