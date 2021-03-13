package mdvrp.ga;

import ga.FitnessCalculator;
import ga.change.Mutator;
import ga.change.Recombinator;
import ga.data.Initializer;
import ga.selection.ParentSelector;
import ga.selection.SurvivorSelector;
import mdvrp.Customer;
import mdvrp.MDVRP;

import java.util.*;
import java.util.stream.Collectors;

public class GeneticAlgorithm {

    private MDVRP problem;
    private float swappingDistance;
    private float unfeasibilityFee;

    private Initializer<Chromosome> initializer;
    private Recombinator<Chromosome> recombinator;
    private Mutator<Chromosome> mutator;
    private ParentSelector<Chromosome> parentSelector;
    private SurvivorSelector<Chromosome> survivorSelector;
    private FitnessCalculator<Chromosome> fitnessCalculator;


    public GeneticAlgorithm(Initializer<Chromosome> initializer,
                            Recombinator<Chromosome> recombinator,
                            Mutator<Chromosome> mutator,
                            ParentSelector<Chromosome> parentSelector,
                            SurvivorSelector<Chromosome> survivorSelector,
                            FitnessCalculator<Chromosome> fitnessCalculator,
                            float swappingDistance, float unfeasibilityFee, MDVRP problem) {

        this.initializer = initializer;
        this.recombinator = recombinator;
        this.mutator = mutator;
        this.parentSelector = parentSelector;
        this.survivorSelector = survivorSelector;
        this.fitnessCalculator = fitnessCalculator;
        this.problem = problem;
        this.swappingDistance = swappingDistance;
        this.unfeasibilityFee = unfeasibilityFee;
    }
}
