package mdvrp.ga;

import java.util.Collections;
import java.util.List;
import java.util.Map;

// TODO: Naming scheme agreement
public class Chromosome {
    private boolean feasible = true;
    private Map<Integer, List<Integer>> genes;

    public Chromosome(Map<Integer, List<Integer>> customersPerDepot, boolean shuffle) {
        // https://stackoverflow.com/questions/8559092/create-an-array-of-arraylists
        genes = customersPerDepot;
        if (shuffle) {
            customersPerDepot.values().forEach(Collections::shuffle);
        }
    }

    public Map<Integer, List<Integer>> getGenes() {
        return Collections.unmodifiableMap(genes);
    }

    public void setFeasible(boolean feasible) {
        this.feasible = feasible;
    }

    public boolean getFeasible() {
        return feasible;
    }
}