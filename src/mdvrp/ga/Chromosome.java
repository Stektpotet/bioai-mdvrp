package mdvrp.ga;

import java.util.ArrayList;
import java.util.List;

// TODO: Naming scheme agreement
public class Chromosome {
    private ArrayList<Integer>[] geneStrings;
    public Chromosome(int numDepots) {
        // https://stackoverflow.com/questions/8559092/create-an-array-of-arraylists
        geneStrings = (ArrayList<Integer>[]) new ArrayList<?>[numDepots];
    }
}

