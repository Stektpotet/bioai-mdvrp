package mdvrp.ga;

import ga.selection.SurvivorSelector;

import java.util.List;

public class SurvivorSelectorMDVRP implements SurvivorSelector<PopulationMDVRP, ChromosomeMDVRP> {

    @Override
    public PopulationMDVRP select(PopulationMDVRP generation, List<ChromosomeMDVRP> parents, List<ChromosomeMDVRP> offspring) {
        List<ChromosomeMDVRP> individuals = generation.getIndividuals();

        // find best one percent of generation - elite
        //TODO: Utilize Population.GetOptimum -> Move to UtilChromosomeMDVRP ?
        ChromosomeMDVRP elite = Util.ArgMin(individuals);

        // take parents out of generation
        for (var p : parents) {
            individuals.remove(p);
        }

        // put offspring into generation
        individuals.addAll(offspring);

        // randomly pop one percent of generation - r
        ChromosomeMDVRP randomlyChosen = individuals.remove(Util.random.nextInt(individuals.size()));

        // put best 50 % of elite and r back into the population
        individuals.add((elite.fitness() < randomlyChosen.fitness()) ? elite : randomlyChosen);

        return generation;

    }
}
