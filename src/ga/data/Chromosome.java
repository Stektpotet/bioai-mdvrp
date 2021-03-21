package ga.data;

import mdvrp.MDVRP;

public interface Chromosome<Problem> {
    public float fitness(Problem problem);
    public boolean isFeasible(Problem problem);
}
