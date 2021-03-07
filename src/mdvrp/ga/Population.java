package mdvrp.ga;

import mdvrp.Customer;
import mdvrp.Depot;
import mdvrp.MDVRP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Population {
    private Map<Customer, List<Depot>> swappingMap;
    private Map<Depot, List<Customer>> customerAssignment;

    private Chromosome[] individuals;

    public Population(int size, List<Depot> depots, List<Customer> customers, float swappingDistance) {
        customerAssignment = new HashMap<>(depots.size());
        swappingMap = new HashMap<>();
        individuals = new Chromosome[size];

        for (Depot d : depots)
            customerAssignment.put(d, new ArrayList<>());

        evaluateDistances(depots, customers, swappingDistance);
        actuallyMakeTheChromosomes();
    }


    public Chromosome[] getIndividuals() {
        return individuals;
    }

    public int getSize() {
        return individuals.length;
    }

    public Map<Depot, List<Customer>> copyAssignment() {
        Map<Depot, List<Customer>> copy = new HashMap<>(customerAssignment.size());
        for (Depot d : customerAssignment.keySet()) {
            List<Customer> depotList = customerAssignment.get(d);
            ArrayList<Customer> depotListCopy = new ArrayList<>(depotList.size());
            copy.put(d, depotListCopy);
            depotListCopy.addAll(depotList);
        }
        return copy;
    }

    private void actuallyMakeTheChromosomes() {
        for (int i = 0; i < getSize(); i++) {
            // TODO: Potentially optimizable if the swappableMap is empty
            //       Just convert the customerAssignment to ids
            Map<Depot, List<Customer>> fullAssignment = copyAssignment();

            // 1. Move from swappingmap into full assignment
            for (Map.Entry<Customer, List<Depot>> entry : swappingMap.entrySet())
            {
                List<Depot> swapOptions = entry.getValue();
                Depot selectedDepot = swapOptions.get(GeneticAlgorithm.rand.nextInt(swapOptions.size()));
                fullAssignment.get(selectedDepot).add(entry.getKey());
            }

            // 2. Convert to IDs
            List<List<Integer>> protoChromosome = new ArrayList<>();
            for (Map.Entry<Depot, List<Customer>> entry : fullAssignment.entrySet())
            {

                List<Integer> gene = new ArrayList<>();
                for (Customer c : entry.getValue())
                    gene.add(c.getId());

                protoChromosome.add(gene);
            }
            individuals[i] = new Chromosome(protoChromosome, true);
        }
    }

    private void evaluateDistances(List<Depot> depots, List<Customer> customers, float swappingDistance) { // -> List<Chromosome>

        for (Customer c : customers)
        {
            Map<Depot, Float> distanceMap = new HashMap<>(depots.size());
            float minimumDistance = Float.MAX_VALUE;
            Depot closestDepot = depots.get(0);
            for (Depot d : depots) {
                float distance = Util.euclideanDistance(d, c);
                if (distance < minimumDistance)
                {
                    minimumDistance = distance;
                    closestDepot = d;
                }
                distanceMap.put(d, distance);
            }

            List<Depot> depotsInSwappingDistance = new ArrayList<>();

            for (Depot d1 : depots) {
                if (closestDepot == d1)
                    continue;
                float comparison = distanceMap.get(d1) - minimumDistance;

                if (Math.abs(comparison) < swappingDistance) {
                    depotsInSwappingDistance.add(d1);
                }
            }

            if (depotsInSwappingDistance.size() > 0) {
                depotsInSwappingDistance.add(closestDepot);
                swappingMap.put(c, depotsInSwappingDistance);
            } else {
                customerAssignment.get(closestDepot).add(c);
            }
        }
    }
}
