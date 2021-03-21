package ga;

import ga.data.Chromosome;


/**
 * A snapshot of the current state of the genetic algorithm, representing the best
 * @param <C>
 */
public class GeneticAlgorithmSnapshot<C extends Chromosome> {
    public final int currentGeneration;
    public final C optimum;
    GeneticAlgorithmSnapshot(int currentGeneration, C optimum) {
        this.currentGeneration = currentGeneration;
        this.optimum = optimum;
    }
}
