package ga.change;

import ga.data.Chromosome;

import java.util.List;

public interface Mutator<C extends Chromosome> {
    public default List<C> mutateAll(List<C> chromosomes) {
        for (C c : chromosomes) {
            mutate(c);
        }
        return chromosomes;
    }
    C mutate(C chromosome);
}
