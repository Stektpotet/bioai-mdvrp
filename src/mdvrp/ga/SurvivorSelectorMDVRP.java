package mdvrp.ga;

import ga.selection.SurvivorSelector;
import mdvrp.MDVRP;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SurvivorSelectorMDVRP implements SurvivorSelector<MDVRP, PopulationMDVRP, ChromosomeMDVRP> {

    MDVRP problem;

    public SurvivorSelectorMDVRP(MDVRP problem) {
        this.problem = problem;
    }

    @Override
    public PopulationMDVRP select(PopulationMDVRP generation, List<ChromosomeMDVRP> parents, List<ChromosomeMDVRP> offspring) {
        List<ChromosomeMDVRP> individuals = generation.getIndividuals();

        // find best one percent of generation - elite
        ChromosomeMDVRP elite = Collections.min(generation.getIndividuals(), UtilChromosomeMDVRP.chromosomeFitnessComparator(problem));

        // take parents out of generation
        for (var p : parents) {
            individuals.remove(p);
        }

        // put offspring into generation
        individuals.addAll(offspring);

        // randomly pop one percent of generation - r
        ChromosomeMDVRP randomlyChosen = individuals.remove(Util.random.nextInt(individuals.size()));

        // put best 50 % of elite and r back into the population
        individuals.add((elite.fitness(problem) < randomlyChosen.fitness(problem)) ? elite : randomlyChosen);

        return generation;

    }
}
