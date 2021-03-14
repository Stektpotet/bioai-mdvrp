package mdvrp.ga;

import ga.data.Chromosome;
import mdvrp.Customer;
import mdvrp.Depot;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class Util {
    public static Random random = new Random(69);
    public static float euclid(Customer a, Customer b) {
        float x = a.getX() - b.getX();
        float y = a.getY() - b.getY();
        x *= x;
        y *= y;
        return x + y;
    }


    public static <C extends Chromosome> C ArgMin(List<C> arr) {
        float minFitness = arr.get(0).fitness();
        C fittest = arr.get(0);
        for (C c : arr){
            float currentFitness = c.fitness();
            if (currentFitness < minFitness) {
                minFitness = currentFitness;
                fittest = c;
            }
        }
        return fittest;
    }

    private static boolean isAssignmentCapacityValid(Map<Depot, List<Customer>> assignment, int numVehicles) {
        int i = 0;
        for (var gene : assignment.entrySet()) {
            int depotRoutesSum = gene.getValue().stream().mapToInt(Customer::getDemand).sum();
            int depotCapacity = gene.getKey().getMaxVehicleLoad() * numVehicles;
            if (depotRoutesSum > depotCapacity)
                return false;
        }
        return true;
    }

}
