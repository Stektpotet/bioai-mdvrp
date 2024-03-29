package mdvrp;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import mdvrp.ga.ChromosomeMDVRP;
import mdvrp.ga.UtilChromosomeMDVRP;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MDVRPFiles {
    public static MDVRP ReadFile(String filepath) {
        System.out.println(String.format("Reading \"%s\"...", filepath));
        try {
            File file = new File(filepath);
            Scanner fileReader = new Scanner(file);
            int maxVehicles = fileReader.nextInt();
            int numCustomers = fileReader.nextInt();
            int numDepots = fileReader.nextInt();
            fileReader.nextLine();
            MDVRP problem = new MDVRP(file.getName(), maxVehicles, numCustomers, numDepots);
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
                Depot depot = new Depot();
                depot.setId(fileReader.nextInt());
                depot.setX(fileReader.nextInt());
                depot.setY(fileReader.nextInt());
                depot.setDuration(fileReader.nextInt());
                depot.setDemand(fileReader.nextInt());
                depot.setMaxDuration(maxDuration[i]);
                depot.setMaxVehicleLoad(maxLoad[i]);
                depots.put(depot.getId(), depot);
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

    public static void WriteImg(WritableImage snapshot, String filePath) {
        File file = new File(filePath);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
        }catch (IOException ignored) {

        }
    }
    public static void WriteFile(MDVRP problem, ChromosomeMDVRP chromosome, String fileName) {
        StringBuilder result = new StringBuilder(String.format("%.2f\n", chromosome.fitness(problem)));
        var solution = chromosome.getSolution(problem);

        var customers = problem.getCustomers();
        var depots = problem.getDepots();

        int depotNr = 1;    // NOTE: not an ID!
        for (var gene : solution.entrySet()) {
            Depot depot = depots.get(gene.getKey());
            int vehicleNr = 1; // NOTE: not an ID!
            for (var route : gene.getValue()) {
                if (route.size() == 0)
                    continue;
                var routeStr = String.format("%d\t%d\t%4.2f\t%3d\t\t%s\n", depotNr, vehicleNr,
                        UtilChromosomeMDVRP.routeDuration(depot, route.streamCustomers(customers)),
                        UtilChromosomeMDVRP.routeDemand(route.streamCustomers(customers)),
                        route.stream().map(String::valueOf).collect(Collectors.joining(" ", "0 ", " 0"))
                        );
                result.append(routeStr);
                vehicleNr++;
            }
            depotNr++;
        }
        System.out.print(result.toString());
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(result.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
