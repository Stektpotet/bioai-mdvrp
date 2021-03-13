package mdvrp.ga;

import java.util.List;
import java.util.Map;

public class PopulationMDVRP extends ga.data.Population<Chromosome> {
    private Map<Integer, List<Integer>> swappingMap;

    PopulationMDVRP(Chromosome[] individuals, Map<Integer, List<Integer>> swappingMap) {
        this.individuals = individuals;
        this.swappingMap = swappingMap;
    }

    public Chromosome[] getIndividuals() {
        return individuals;
    }

    int getSize() {
        return individuals.length;
    }

    Map<Integer, List<Integer>> getSwappingMap() {
        return swappingMap;
    }
}
