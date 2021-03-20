package mdvrp;

import mdvrp.ga.ChromosomeMDVRP;
import mdvrp.ga.RouteScheduler;
import mdvrp.structures.Schedule;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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

    public static void WriteFile(MDVRP problem, ChromosomeMDVRP chromosome, String fileName) {
        StringBuilder result = new StringBuilder(String.format("%.2f\n", chromosome.fitness()));
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
                        RouteScheduler.getRouteDuration(depot, route.streamCustomers(customers)),
                        RouteScheduler.routeDemandSum(route.streamCustomers(customers)),
                        route.stream().map(String::valueOf).collect(Collectors.joining(" "))
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
