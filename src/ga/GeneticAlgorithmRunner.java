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

public class GeneticAlgorithmRunner<ProblemT, Pop extends Population<ProblemT, C>, C  extends Chromosome<ProblemT>> extends Service<GeneticAlgorithmSnapshot<C>> {

    private Initializer<ProblemT, Pop, C> initializer;
    private Recombinator<C> recombinator;
    private Mutator<ProblemT, Pop, C> mutator;
    private ParentSelector<ProblemT, C> parentSelector;
    private SurvivorSelector<ProblemT, Pop, C> survivorSelector;
    private final int populationSize;
    private final int numGenerations;

    public GeneticAlgorithmRunner(Initializer<ProblemT, Pop, C> initializer,
                            Recombinator<C> recombinator,
                            Mutator<ProblemT, Pop, C> mutator,
                            ParentSelector<ProblemT, C> parentSelector,
                            SurvivorSelector<ProblemT, Pop, C> survivorSelector, int populationSize, int numGenerations) {
        this.initializer = initializer;
        this.recombinator = recombinator;
        this.mutator = mutator;
        this.parentSelector = parentSelector;
        this.survivorSelector = survivorSelector;
        this.populationSize = populationSize;
        this.numGenerations = numGenerations;
    }

    @Override
    protected Task<GeneticAlgorithmSnapshot<C>> createTask() {
        return new Task<>() {
            @Override
            protected GeneticAlgorithmSnapshot<C> call() throws Exception {
                Pop pop = initializer.breed(populationSize);
                for (int i = 0; i < numGenerations; i++) {
                    List<C> parents = parentSelector.select(pop);
                    List<C> offspring = mutator.mutateAll(pop, recombinator.recombine(parents));
                    pop = survivorSelector.select(pop, parents, offspring);
                    C optimum = pop.getOptimum();
                    updateValue(new GeneticAlgorithmSnapshot<>(i, optimum));
                    updateProgress(i, numGenerations);
                }
                return new GeneticAlgorithmSnapshot<>(numGenerations, pop.getOptimum());
            }
        };
    }

    @Override
    protected void succeeded() {
        System.out.println("Task Completed Successfully!");
    }
}
