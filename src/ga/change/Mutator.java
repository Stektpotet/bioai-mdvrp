package ga.change;

import ga.data.Chromosome;
import ga.data.Population;

import java.util.List;

public interface Mutator<P extends Population<C>, C extends Chromosome> {
    public default List<C> mutateAll(P population, List<C> chromosomes) {
        for (C c : chromosomes) {
            mutate(population, c);
        }
        return chromosomes;
    }
    C mutate(P population, C chromosome);
}
