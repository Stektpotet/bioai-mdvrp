package mdvrp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MDVRPFiles {
    public static MDVRP ReadFile(String filepath) {
        try {
            File file = new File(filepath);
            Scanner fileReader = new Scanner(file);
            int maxVehicles = fileReader.nextInt();
            int numCustomers = fileReader.nextInt();
            int numDepots = fileReader.nextInt();
            System.out.println(String.format("%d %d %d", maxVehicles, numCustomers, numDepots));
            fileReader.nextLine();

            MDVRP problem = new MDVRP(maxVehicles, numCustomers, numDepots);
            Map<Integer, Depot> depots = problem.getDepotsMutable();
            Map<Integer, Customer> customers = problem.getCustomersMutable();

            int[] maxDuration = new int[numDepots];
            int[] maxLoad = new int[numDepots];
            for (int i = 0; i < numDepots; i++) {
                maxDuration[i] = fileReader.nextInt();
                maxLoad[i] = fileReader.nextInt();
                fileReader.nextLine();
            }

            for (int i = 0; i < numCustomers; i++) {
                Customer vertex = new Customer();
                vertex.setId(fileReader.nextInt());
                vertex.setX(fileReader.nextInt());
                vertex.setY(fileReader.nextInt());
                vertex.setDuration(fileReader.nextInt());
                vertex.setDemand(fileReader.nextInt());
                customers.put(vertex.getId(), vertex);
                fileReader.nextLine();
            }
            for (int i = 0; i < numDepots; i++) {
                Depot vertex = new Depot(); // TODO: We actually dont need the ID to be stored in the data anymore
                vertex.setId(fileReader.nextInt());
                vertex.setX(fileReader.nextInt());
                vertex.setY(fileReader.nextInt());
                vertex.setDuration(fileReader.nextInt());
                vertex.setDemand(fileReader.nextInt());
                vertex.setMaxDuration(maxDuration[i]);
                vertex.setMaxVehicleLoad(maxLoad[i]);
                depots.put(vertex.getId(), vertex);
                fileReader.nextLine();
            }
            fileReader.close();
            return problem;
        } catch (FileNotFoundException e) {
            System.out.println(String.format("File: \"%s\" not found!", filepath));
            e.printStackTrace();
        }
        return null;
    }
}
