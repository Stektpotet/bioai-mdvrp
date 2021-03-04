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
            List<Depot> depots = problem.getDepotsMutable();
            List<Customer> vertices = problem.getVerticesMutable();
            for (int i = 0; i < numDepots; i++) {
                Depot depot = depots.get(i);
                depot.setMaxDuration(fileReader.nextInt());
                depot.setMaxVehicleLoad(fileReader.nextInt());
                fileReader.nextLine();
            }

            for (int i = 0; i < numCustomers + numDepots; i++) {
                Customer vertex = vertices.get(i);
                vertex.setId(fileReader.nextInt());
                vertex.setX(fileReader.nextInt());
                vertex.setY(fileReader.nextInt());
                vertex.setDuration(fileReader.nextInt());
                vertex.setDemand(fileReader.nextInt());
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
