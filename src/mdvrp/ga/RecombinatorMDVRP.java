package mdvrp.ga;

import ga.change.Recombinator;

public class RecombinatorMDVRP implements Recombinator<Chromosome> {
    @Override
    public Chromosome[] recombine(Chromosome[] parents) {
        return new Chromosome[0];
    }

    @Override
    public Chromosome[] crossover(Chromosome mama, Chromosome papa) {
        return new Chromosome[0];
    }
}
