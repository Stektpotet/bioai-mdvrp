package mdvrp.ga;

import ga.change.Mutator;

public class ShuffleCustomerOrderMutation implements Mutator<Chromosome> {

    @Override
    public Chromosome mutate(Chromosome chromosome) {
        return new Chromosome(chromosome.getGenes(), true);
    }
}
