package mdvrp.ga;

import mdvrp.Customer;
import mdvrp.Depot;
import mdvrp.MDVRP;

import java.util.*;

public class GeneticAlgorithm {

    public static Random rand = new Random();
    private MDVRP problem;
    private float swappingDistance;

    public GeneticAlgorithm(MDVRP problem, float swappingDistance) {
        this.problem = problem;
        this.swappingDistance = swappingDistance;
    }


}
