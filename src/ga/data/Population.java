package ga.data;


import java.util.List;

/**
 * @param <C> Chromosome
 */
public abstract class Population<ProblemT, C extends Chromosome<ProblemT>> {
    protected List<C> individuals;
    protected ProblemT problem;

    public Population(ProblemT problem, List<C> individuals) {
        this.individuals = individuals;
        this.problem = problem;
    }

    public List<C> getIndividuals() {
        return individuals;
    }

    public C getOptimum() {
        float minFitness = individuals.get(0).fitness(problem);
        C fittest = individuals.get(0);
        for (C c : individuals){
            float currentFitness = c.fitness(problem);
            if (c.isFeasible(problem) && currentFitness < minFitness) {
                minFitness = currentFitness;
                fittest = c;
            }
        }
        return fittest;
    }
}
