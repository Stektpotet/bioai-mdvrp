package mdvrp.ga;

import ga.change.Mutator;
import ga.change.Recombinator;
import ga.data.Chromosome;
import ga.data.Initializer;
import ga.data.Population;
import ga.selection.ParentSelector;
import ga.selection.SurvivorSelector;

import java.util.List;

public class GeneticAlgorithm<Pop extends Population<C>, C  extends Chromosome> {

    private Initializer<Pop, C> initializer;
    private Recombinator<C> recombinator;
    private Mutator<Pop, C> mutator;
    private ParentSelector<C> parentSelector;
    private SurvivorSelector<Pop, C> survivorSelector;


    public GeneticAlgorithm(Initializer<Pop, C> initializer,
                            Recombinator<C> recombinator,
                            Mutator<Pop, C> mutator,
                            ParentSelector<C> parentSelector,
                            SurvivorSelector<Pop, C> survivorSelector) {

        this.initializer = initializer;
        this.recombinator = recombinator;
        this.mutator = mutator;
        this.parentSelector = parentSelector;
        this.survivorSelector = survivorSelector;
    }

    public C run(int populationSize, int numGenerations) {
        Pop pop = initializer.breed(populationSize);
        for (int i = 0; i < numGenerations; i++) {
            List<C> parents = parentSelector.select(pop);
            List<C> offspring = mutator.mutateAll(pop, recombinator.recombine(parents));
            pop = survivorSelector.select(pop, parents, offspring);
            C currentOptimum = pop.getOptimum();
            System.out.println(currentOptimum.fitness());
        }
        System.out.println("END");
        return null;
    }
}
