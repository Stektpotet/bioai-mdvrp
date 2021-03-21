package ga.data;

import mdvrp.MDVRP;

public interface Chromosome<Problem> {
    public double fitness(Problem problem);
    public boolean isFeasible(Problem problem);
}
