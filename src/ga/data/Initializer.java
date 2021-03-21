package ga.data;

public interface Initializer<ProblemT, Pop extends Population<ProblemT, C>, C  extends Chromosome<ProblemT>> {
    public Pop breed(int popSize);
}
