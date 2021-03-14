package mdvrp.ga;

import ga.change.Mutator;

public class ShuffleCustomerOrderMutation implements Mutator<ChromosomeMDVRP> {

    @Override
    public ChromosomeMDVRP mutate(ChromosomeMDVRP chromosome) {
        return new ChromosomeMDVRP(chromosome.getGenes(), true);
    }
}
