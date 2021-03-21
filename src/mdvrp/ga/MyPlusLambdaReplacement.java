package mdvrp.ga;

import ga.data.Population;
import ga.selection.SurvivorSelector;
import mdvrp.MDVRP;

import java.util.ArrayList;
import java.util.List;

public class MyPlusLambdaReplacement implements SurvivorSelector<MDVRP, PopulationMDVRP, ChromosomeMDVRP> {

    MDVRP problem;

    public MyPlusLambdaReplacement(MDVRP problem) {
        this.problem = problem;
    }

    @Override
    public PopulationMDVRP select(PopulationMDVRP generation, List<ChromosomeMDVRP> parents, List<ChromosomeMDVRP> offspring) {
        List<ChromosomeMDVRP> myPlusLa = new ArrayList<>(generation.getIndividuals());
        myPlusLa.addAll(offspring);
        myPlusLa.sort(UtilChromosomeMDVRP.chromosomeFitnessComparator(problem));
        return new PopulationMDVRP(problem, myPlusLa.subList(0, generation.getSize()), generation.getSwappingMap());
    }
}
