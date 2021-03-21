package mdvrp.ga;

import ga.data.Chromosome;
import mdvrp.Customer;
import mdvrp.Depot;
import mdvrp.structures.CustomerSequence;
import mdvrp.structures.Schedule;

import java.util.*;

public class Util {
    public static Random random = new Random(69);

    public static double duration(Customer a, Customer b) {
        float x = a.getX() - b.getX();
        float y = a.getY() - b.getY();
        return Math.sqrt(x*x + y*y);
    }


    static double duration(Customer a, Customer b, Customer c) {
        return Util.duration(a, b) + Util.duration(b, c);
    }

    static <T> T randomChoice(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    static <T> T randomChoice(Set<T> set) {
        int selectionIndex = random.nextInt(set.size());
        var iter = set.iterator();
        for (int i = 0; i < selectionIndex; i++) {
            iter.next();
        }
        return iter.next();
    }

    static <T> T randomChoiceRemove(List<T> list) {
        return list.remove(random.nextInt(list.size()));
    }

    static <T> List<T> randomChoice(final List<T> list, int n, boolean replace) {
        assert replace || n <= list.size();

        if (replace) {
            List<T> chosen = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                chosen.add(list.get(random.nextInt(list.size())));
            }
            return chosen;
        }
        List<T> chosen = new ArrayList<>(list);
        Collections.shuffle(chosen, random);
        return chosen.subList(0, n);
    }

    public static void main(String[] args) {
        List<Float> test = new ArrayList<>();
        test.add(1f);
        test.add(6f);
        test.add(7f);
        test.add(8f);
        test.add(2f);
        test.add(3f);
        test.add(4f);
        test.add(5f);

        Comparator<Float> fitnessComp = (a, b) -> (int) Math.signum(a - b);
        System.out.println(Collections.max(test, fitnessComp));
    }

    static <T> List<T> randomChoiceNoReplacement(List<T> list, int n) { // TODO: Not nice
        List<T> choice = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            choice.add(list.remove(random.nextInt(list.size())));
        }
        return choice;
//        return list.get(random.nextInt(list.size()));
    }
//    static <K, V> V randomChoice(Map<K, V> map) {
//        return randomChoice(new ArrayList<>(map.values())); // TODO: Not nice
//    }


    /*private static boolean isAssignmentCapacityValid(Map<Depot, List<Customer>> assignment, int numVehicles) {
        int i = 0;
        for (var gene : assignment.entrySet()) {
            int depotRoutesSum = gene.getValue().stream().mapToInt(Customer::getDemand).sum();
            int depotCapacity = gene.getKey().getMaxVehicleLoad() * numVehicles;
            if (depotRoutesSum > depotCapacity)
                return false;
        }
        return true;
    }*/

}
