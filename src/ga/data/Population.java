package ga.data;


import mdvrp.ga.ChromosomeMDVRP;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <C> Chromosome
 */
public abstract class Population<C extends Chromosome> {
    protected List<C> individuals;

    public List<C> getIndividuals() {
        return individuals;
    }

    public C getOptimum() {
        float minFitness = individuals.get(0).fitness();
        C fittest = individuals.get(0);
        for (C c : individuals){
            float currentFitness = c.fitness();
            if (currentFitness < minFitness) {
                minFitness = currentFitness;
                fittest = c;
            }
        }
        return fittest;
    }
}
