package mdvrp.ga;

import ga.data.Initializer;
import mdvrp.Customer;
import mdvrp.Depot;
import mdvrp.MDVRP;

import java.util.*;
import java.util.stream.Collectors;

public class Breeder implements Initializer<Chromosome> {

    private MDVRP problem;
    private float swappingDistance;
    private Map<Integer, List<Integer>> fixedAssignment;
    private Map<Integer, List<Integer>> swappingMap;


    public Breeder(MDVRP problem, float swappingDistance) {
        this.problem = problem;
        this.swappingDistance = swappingDistance;

        Map<Integer, Depot> depots = this.problem.getDepots();

        fixedAssignment = new HashMap<>();
        for (Depot d : depots.values())
            fixedAssignment.put(d.getId(), new ArrayList<>());

        swappingMap = new HashMap<>();

    }

    @Override
    public Population breed(int popSize) {
        //TODO: maybe, that is not nice! - Klara
        for (var customerList : fixedAssignment.values())
            customerList.clear();
        for (var depotList : swappingMap.values())
            depotList.clear();

        evaluateDistances();
        return actuallyMakeTheChromosomes(popSize);
    }

    private void evaluateDistances() { // -> List<Chromosome>
        Map<Integer, Customer> customers = problem.getCustomers();
        Map<Integer, Depot> depots = problem.getDepots();

        // reusable distanceMap
        Map<Depot, Float> distanceMap = new HashMap<>(depots.size());

        // create a map from depots to distances
        for (Customer c : customers.values())
        {
            // initialise closest
            float minimumDistance = Float.MAX_VALUE;
            Depot closestDepot = depots.get(0);

            for (Depot d : depots.values()) {
                float distance = Util.euclid(d, c);
                distanceMap.put(d, distance);

                // update closest
                if (distance < minimumDistance)
                {
                    minimumDistance = distance;
                    closestDepot = d;
                }
            }

            List<Depot> depotsInSwappingDistance = depotsInSwappingDistance(distanceMap, minimumDistance, closestDepot);

            if (depotsInSwappingDistance.size() > 0) {
                depotsInSwappingDistance.add(closestDepot);
                swappingMap.put(c.getId(), depotsInSwappingDistance.stream().map(Depot::getId).collect(Collectors.toList()));
            } else {
                fixedAssignment.get(closestDepot.getId()).add(c.getId());
            }

            distanceMap.clear();
        }
    }

    private List<Depot> depotsInSwappingDistance(Map<Depot, Float> distanceMap, float minimumDistance, Depot closestDepot) {
        Map<Integer, Depot> depots = problem.getDepots();
        List<Depot> depotsInSwappingDistance = new ArrayList<>();

        // if the distance to the current depot is within the minimumDistance + swappingDistance, add to listOfDepots in swapping distance
        for (Depot d1 : depots.values()) {
            if (closestDepot == d1)
                continue;
            float comparison = distanceMap.get(d1) - minimumDistance;

            if (Math.abs(comparison) < swappingDistance) {
                depotsInSwappingDistance.add(d1);
            }
        }
        return depotsInSwappingDistance;
    }

    private Map<Integer, List<Integer>> copyAssignment() {
        Map<Integer, List<Integer>> copy = new HashMap<>(fixedAssignment.size());
        for (Integer depotId : fixedAssignment.keySet()) {
            List<Integer> customersOfDepot = fixedAssignment.get(depotId);
            ArrayList<Integer> customersOfDepoCopy = new ArrayList<>(customersOfDepot.size());
            customersOfDepoCopy.addAll(customersOfDepot);
            copy.put(depotId, customersOfDepoCopy);
        }
        return copy;
    }


    private mdvrp.ga.Population actuallyMakeTheChromosomes(int popSize) {
        Chromosome[] individuals = new Chromosome[popSize];
        for (int i = 0; i < popSize; i++) {
            // TODO: Potentially optimizable if the swappableMap is empty
            //       Just convert the customerAssignment to ids
            Map<Integer, List<Integer>> fullAssignment = copyAssignment();

            // 1. Move from swappingmap into full assignment
            for (var entry : swappingMap.entrySet()) {
                List<Integer> swapOptions = entry.getValue();
                Integer selectedDepot = swapOptions.get(Util.random.nextInt(swapOptions.size()));
                fullAssignment.get(selectedDepot).add(entry.getKey());
            }

            // 2. Check Capacity feasibility of depot customer assignments
//            isAssignmentCapacityValid(fullAssignment, )

            // 3. Convert to IDs
            Map<Integer, List<Integer>> protoChromosome = new HashMap<>();
            for (Map.Entry<Integer, List<Integer>> entry : fullAssignment.entrySet()) {

                List<Integer> geneString = new ArrayList<>(entry.getValue());
                protoChromosome.put(entry.getKey(), geneString);
            }
            individuals[i] = new Chromosome(protoChromosome, true);
        }
        return new mdvrp.ga.Population(individuals, swappingMap);
    }
}
