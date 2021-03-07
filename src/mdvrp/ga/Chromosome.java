package mdvrp.ga;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

// TODO: Naming scheme agreement
public class Chromosome {
    private Map<Integer, List<Integer>> geneStrings;

    public Chromosome(Map<Integer, List<Integer>> customersPerDepot, boolean shuffle) {
        // https://stackoverflow.com/questions/8559092/create-an-array-of-arraylists
        geneStrings = customersPerDepot;
        if (shuffle) {
            for (List<Integer> depotRoute : customersPerDepot.values())
                Collections.shuffle(depotRoute);
        }
    }

    public Map<Integer, List<Integer>> getGeneStrings() {
        return geneStrings;
    }
}