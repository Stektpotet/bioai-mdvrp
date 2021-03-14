package ga.change;

import ga.data.Chromosome;

import java.util.List;

public interface Recombinator<C extends Chromosome> {
    public List<C> recombine(List<C> parents);
    List<C> crossover(C mum, C dad);
}
