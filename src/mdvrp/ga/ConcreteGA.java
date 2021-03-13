package mdvrp.ga;

import mdvrp.MDVRP;

public class ConcreteGA {

    private MDVRP problem;
    private float swappingDistance;
    private float unfeasibilityFee;

    private Breeder initializer;
    private RecombinatorMDVRP recombinator;
    private MutatorMDVRP mutator;
    private ParentSelectorMDVRP parentSelector;
    private SurvivorSelectorMDVRP survivorSelector;
    //private FitnessCalculator<C> fitnessCalculator;


    public ConcreteGA(Breeder initializer,
                            RecombinatorMDVRP recombinator,
                            MutatorMDVRP mutator,
                            ParentSelectorMDVRP parentSelector,
                            SurvivorSelectorMDVRP survivorSelector,
                            //FitnessCalculator<C> fitnessCalculator,
                            MDVRP problem) {

        this.initializer = initializer;
        this.recombinator = recombinator;
        this.mutator = mutator;
        this.parentSelector = parentSelector;
        this.survivorSelector = survivorSelector;
        //this.fitnessCalculator = fitnessCalculator;
        this.problem = problem;
    }

    public Chromosome run(int populationSize, int numGenerations) {
        PopulationMDVRP pop = initializer.breed(populationSize);
        for (int i = 0; i < numGenerations; i++) {
            Chromosome[] parents = parentSelector.select(pop);
            Chromosome[] offspring = recombinator.recombine(parents);
            pop = survivorSelector.select(pop, parents, offspring);
        }
        return null;
    }
}

