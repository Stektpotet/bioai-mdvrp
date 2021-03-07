package mdvrp.ga;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO: Naming scheme agreement
public class Chromosome {
    private List<List<Integer>> geneStrings;

    public Chromosome(List<List<Integer>> customersPerDepot, boolean shuffle) {
        // https://stackoverflow.com/questions/8559092/create-an-array-of-arraylists
        geneStrings = customersPerDepot;
        if (shuffle) {
            for (List<Integer> depotRoute : customersPerDepot)
                Collections.shuffle(depotRoute);
        }
    }

    public List<List<Integer>> getGeneStrings() {
        return geneStrings;
    }
}