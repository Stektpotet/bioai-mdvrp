package ga;

import ga.change.Mutator;
import ga.change.Recombinator;
import ga.data.Chromosome;
import ga.data.Initializer;
import ga.data.Population;
import ga.selection.ParentSelector;
import ga.selection.SurvivorSelector;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.List;

public class GeneticAlgorithmRunner<Pop extends Population<C>, C  extends Chromosome> extends Service<C> {

    private Initializer<Pop, C> initializer;
    private Recombinator<C> recombinator;
    private Mutator<Pop, C> mutator;
    private ParentSelector<C> parentSelector;
    private SurvivorSelector<Pop, C> survivorSelector;
    private final int populationSize;
    private final int numGenerations;

    public GeneticAlgorithmRunner(Initializer<Pop, C> initializer,
                            Recombinator<C> recombinator,
                            Mutator<Pop, C> mutator,
                            ParentSelector<C> parentSelector,
                            SurvivorSelector<Pop, C> survivorSelector, int populationSize, int numGenerations) {

        this.initializer = initializer;
        this.recombinator = recombinator;
        this.mutator = mutator;
        this.parentSelector = parentSelector;
        this.survivorSelector = survivorSelector;
        this.populationSize = populationSize;
        this.numGenerations = numGenerations;
    }

    @Override
    protected Task<C> createTask() {
        return new Task<C>() {
            @Override
            protected C call() throws Exception {
                Pop pop = initializer.breed(populationSize);
                for (int i = 0; i < numGenerations; i++) {
                    List<C> parents = parentSelector.select(pop);
                    List<C> offspring = mutator.mutateAll(pop, recombinator.recombine(parents));
                    pop = survivorSelector.select(pop, parents, offspring);
                    C optimum = pop.getOptimum();
                    updateValue(optimum);

                }
                return pop.getOptimum();
            }
        };
    }

    @Override
    protected void succeeded() {
        System.out.println("Task Completed Successfully!");
    }
}
