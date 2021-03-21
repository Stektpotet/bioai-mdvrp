package mdvrp.ga;

import mdvrp.MDVRP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PopulationMDVRP extends ga.data.Population<MDVRP, ChromosomeMDVRP> {
    private Map<Integer, List<Integer>> swappingMap;

    PopulationMDVRP(MDVRP problem, List<ChromosomeMDVRP> individuals, Map<Integer, List<Integer>> swappingMap) {
        super(problem, individuals);
        this.swappingMap = swappingMap;
    }

    int getSize() {
        return individuals.size();
    }

    Map<Integer, List<Integer>> getSwappingMap() {
        return swappingMap;
    }
}
