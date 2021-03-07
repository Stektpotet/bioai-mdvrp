package sample;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import mdvrp.Customer;
import mdvrp.Depot;
import mdvrp.MDVRP;
import mdvrp.MDVRPFiles;
import mdvrp.ga.Chromosome;
import mdvrp.ga.Population;


import java.util.*;

public class Main extends Application {

    static final Paint DEPOT_COLOR = Paint.valueOf("f16");
    static final Paint CUSTOMER_COLOR = Paint.valueOf("5ad");

    @Override
    public void start(Stage primaryStage) throws Exception{
        MDVRP problem = MDVRPFiles.ReadFile("res/problems/p21");
        if (problem == null)
            return;

        // TODO: I think the our GA should be running using Platform.runLater()
        // TODO: tutorials.jenkov.com/javafx/concurrency.html

        primaryStage.setTitle("Multi-Depot Vehicle Routing Problem Visualizer");

        Population pop = new Population(4, problem.getDepots(), problem.getCustomers(), 2000);

        Chromosome[] individuals = pop.getIndividuals();

        Chromosome chromosome = individuals[0];

        Map<Integer, List<Integer>> geneStrings = chromosome.getGenes();

        Color[] colors = {Color.GREEN, Color.FIREBRICK, Color.YELLOW, Color.DARKCYAN, Color.AQUA,
                Color.DARKSALMON, Color.MAROON, Color.PEACHPUFF};
        int i = 0;
        List<Node> nodes = new ArrayList<>();
        List<Customer> customers = problem.getCustomers();
        for (List<Integer> depotAssignment : geneStrings.values())
        {
            Color color = colors[i++ % colors.length];
            for (Integer customerID : depotAssignment) {
                Customer c = customers.get(customerID - 1);
                Circle node = new Circle(c.getX(), c.getY(),1, color);
                nodes.add(node);
            }
        }
//        for (Customer c : problem.getCustomers()) {
//            Circle node = new Circle(c.getX(), c.getY(),1, CUSTOMER_COLOR);
//            nodes.add(node);
//            System.out.println(c);
//        }
        for (Depot d : problem.getDepots()) {
            Rectangle node = new Rectangle(d.getX(), d.getY(),3, 3);
            node.setFill(DEPOT_COLOR);
            nodes.add(node);
            System.out.println(d);
        }
        Group vertices = new Group(nodes);

        // Scale and move the "board" into view, making it cover the window
        Bounds bounds = vertices.getBoundsInLocal();
        vertices.getTransforms().addAll(
                new Scale(896f / bounds.getWidth(), 896f / bounds.getHeight()),
                new Translate(-bounds.getMinX(), -bounds.getMinY())
        );
        Group root = new Group(vertices);
        Scene scene = new Scene(root, 896, 896,  Color.BLACK);

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
    }
}
