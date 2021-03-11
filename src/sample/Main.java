package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import mdvrp.Customer;
import mdvrp.Depot;
import mdvrp.MDVRP;
import mdvrp.MDVRPFiles;
import mdvrp.ga.Population;
import mdvrp.ga.RouteScheduler;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main extends Application {

    static final int SCREEN_WIDTH = 896, SCREEN_HEIGHT = 896;

    static final Color BACKGROUND_COLOR = Color.BLACK;

    static final Paint DEPOT_COLOR = Paint.valueOf("f16");
    static final Paint CUSTOMER_COLOR = Paint.valueOf("5ad");
    static final Paint LINE_COLOR = Paint.valueOf("baddad");

    static final Color[] COLOR_CYCLE = {Color.GREEN, Color.FIREBRICK, Color.YELLOW, Color.DARKCYAN, Color.AQUA,
            Color.DARKSALMON, Color.MAROON, Color.PEACHPUFF};

    GraphicsContext graphics;
    UpdateLoop updateLoop;

    Population population;
    MDVRP problem;

    // BASED ON THIS
    // https://jvm-gaming.org/t/looking-for-the-simplest-practical-javafx-game-loop/55903
    private class UpdateLoop extends AnimationTimer {

        private long before = System.nanoTime();

        @Override
        public void handle(long now) {
            float delta = (float) ((now - before) / 1E9);
            graphics.setFill(BACKGROUND_COLOR);
            graphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            updateAndRender(delta);
            before = now;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (!initializeProblem())
            return;

        Bounds bounds = calculateProblemBounds(problem);
        Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        graphics = canvas.getGraphicsContext2D();
        double largeAxis = Math.max(bounds.getHeight(), bounds.getWidth());
        Affine transformationMatrix = new Affine();
        transformationMatrix.appendScale(SCREEN_WIDTH / largeAxis, SCREEN_HEIGHT / largeAxis);
        transformationMatrix.appendTranslation(
                -bounds.getMinX() + (largeAxis - bounds.getWidth()) / 2,
                -bounds.getMinY() + (largeAxis - bounds.getHeight()) / 2);
        graphics.setTransform(transformationMatrix);

        primaryStage.setTitle("Multi-Depot Vehicle Routing Problem Visualizer");
        Group root = new Group(canvas); //drawSchedule(schedule, problem.getCustomers(), problem.getDepots());

        updateLoop = new UpdateLoop();
        updateLoop.start();

        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.BLACK);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private boolean initializeProblem() {
        problem = MDVRPFiles.ReadFile("res/problems/p01");
        if (problem == null)
            return false;
        population = new Population(1, problem.getDepots(), problem.getCustomers(), 0);

        return true;
    }

    private void updateAndRender(float delta) {
        // TODO: get fittest individual
        var schedule = RouteScheduler.scheduleRoutes(population.getIndividuals()[0], problem);
        drawRoutes(schedule, problem.getCustomers(), problem.getDepots());
        drawProblem(problem);
    }

    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
    }


//
//    private void drawChromosomeBackup(Stage primaryStage, MDVRP problem, Chromosome chromosome) {
//
//
//        List<Map<Integer, List<List<Integer>>>> scheduledRoutesPerDepot = Arrays.stream(individuals).map(
//                c -> RouteScheduler.scheduleRoutes(c, problem)
//        ).collect(Collectors.toList());
//
//        System.out.println(Arrays.stream(individuals).mapToInt(c -> c.getFeasible() ? 1 : 0).sum());
//
//        Map<Integer, List<Integer>> geneStrings = chromosome.getGenes();
//
//        Color[] colors = {Color.GREEN, Color.FIREBRICK, Color.YELLOW, Color.DARKCYAN, Color.AQUA,
//                Color.DARKSALMON, Color.MAROON, Color.PEACHPUFF};
//        int i = 0;
//        List<Node> nodes = new ArrayList<>();
//        Map<Integer, Customer> customers = problem.getCustomers();
//        for (List<Integer> depotAssignment : geneStrings.values())
//        {
//            Color color = colors[i++ % colors.length];
//            for (Integer customerID : depotAssignment) {
//                Customer c = customers.get(customerID);
//                Circle node = new Circle(c.getX(), c.getY(),1, color);
//                nodes.add(node);
//            }
//        }
////        for (Customer c : problem.getCustomers()) {
////            Circle node = new Circle(c.getX(), c.getY(),1, CUSTOMER_COLOR);
////            nodes.add(node);
////            System.out.println(c);
////        }
//        for (Depot d : problem.getDepots().values()) {
//            Rectangle node = new Rectangle(d.getX(), d.getY(),3, 3);
//            node.setFill(DEPOT_COLOR);
//            nodes.add(node);
//            System.out.println(d);
//        }
//        Group vertices = new Group(nodes);
//
//        // Scale and move the "board" into view, making it cover the window
//
//
//    }
//
    private BoundingBox calculateProblemBounds(MDVRP problem) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (var customer : problem.getCustomers().values()) {
            int x = customer.getX();
            int y = customer.getY();
            if (minX > x)
                minX = x;
            if (minY > y)
                minY = y;
            if (maxX < x)
                maxX = x;
            if (maxY < y)
                maxY = y;
        }
        for (var depot : problem.getDepots().values()) {
            int x = depot.getX();
            int y = depot.getY();
            if (minX > x)
                minX = x;
            if (minY > y)
                minY = y;
            if (maxX < x)
                maxX = x;
            if (maxY < y)
                maxY = y;
        }
        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }

    private void drawProblem(MDVRP problem) {
        graphics.setFill(CUSTOMER_COLOR);
        for (var customer : problem.getCustomers().values()) {
            graphics.fillOval(customer.getX(), customer.getY(), .5, .5);
        }
        graphics.setFill(DEPOT_COLOR);
        for (var depot : problem.getDepots().values()) {
            graphics.fillRect(depot.getX(), depot.getY(), 1, 1);
        }
    }

    private void drawRoutes(Map<Integer, List<List<Integer>>> schedule, Map<Integer, Customer> customers,
                            Map<Integer, Depot> depots) {
        for (var routesPerDepot : schedule.entrySet()) {
            Depot depot = depots.get(routesPerDepot.getKey());
            int colorCycler = 0;
            for (var route : routesPerDepot.getValue()) {
                Customer current = depot;
                graphics.setStroke(COLOR_CYCLE[colorCycler]);
                for (var nextCustomerID : route) {
                    Customer next = customers.get(nextCustomerID);
                    drawLine(current, next);
                    current = next;
                }
                drawLine(current, depot);
                colorCycler = (colorCycler + 1) % COLOR_CYCLE.length;
            }
        }
    }

    private void drawLine(Customer start, Customer end) {
        graphics.setLineWidth(0.25);
        graphics.strokeLine(start.getX() + .25, start.getY() + .25, end.getX() + .25, end.getY() + .25);
    }

    private Group drawProblemOld(MDVRP problem) {
        List<Node> nodes = new ArrayList<>();
        for (var customer : problem.getCustomers().values()){
            Circle node = new Circle(customer.getX(), customer.getY(),1, CUSTOMER_COLOR);
            nodes.add(node);
        }

        for (var depot : problem.getDepots().values()){
            Rectangle node = new Rectangle(depot.getX(), depot.getY(),2, 2);
            node.setFill(DEPOT_COLOR);
            nodes.add(node);
        }
        return new Group(new Group(nodes));
    }

    private Group drawScheduleOld(Map<Integer, List<List<Integer>>> schedule, Map<Integer, Customer> customers,
                               Map<Integer, Depot> depots) {
        List<Node> nodes = new ArrayList<>();

        for (var routesPerDepot : schedule.entrySet()) {
            Customer depot = depots.get(routesPerDepot.getKey());
            int cycler = 0;
            for (List<Integer> route : routesPerDepot.getValue()) {
                Customer position = depot; // start route add depo
                for (Customer base : route.stream().map(customers::get).collect(Collectors.toList())) {
                    nodes.add(makeLineNode(position, base, COLOR_CYCLE[cycler]));
                    position = base;
                }
                nodes.add(makeLineNode(position, depot, COLOR_CYCLE[cycler]));
                cycler = (cycler + 1) % COLOR_CYCLE.length;
            }
        }
        return new Group(new Group(nodes));
    }

    private Line makeLineNode(Customer start, Customer end, Paint color) {
        Line l = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        l.setStroke(color);
        l.setStrokeWidth(0.25);
        return l;
    }
}
