package mdvrp.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PopulationMDVRP extends ga.data.Population<ChromosomeMDVRP> {
    private Map<Integer, List<Integer>> swappingMap;

    PopulationMDVRP(List<ChromosomeMDVRP> individuals, Map<Integer, List<Integer>> swappingMap) {
        this.individuals = individuals;
        this.swappingMap = swappingMap;
    }

    int getSize() {
        return individuals.size();
    }

    Map<Integer, List<Integer>> getSwappingMap() {
        return swappingMap;
    }
}
