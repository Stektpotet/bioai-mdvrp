package sample;

import ga.data.Initializer;
import ga.data.Population;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mdvrp.MDVRP;
import mdvrp.MDVRPFiles;
import mdvrp.ga.*;
import mdvrp.visual.MDVRPVisualizer;

public class Main extends Application {

    static final int SCREEN_WIDTH = 896, SCREEN_HEIGHT = 896;

    MDVRPVisualizer visualizer;
    mdvrp.ga.Population population;
    MDVRP problem;
    ShuffleCustomerOrderMutation shuffler;
    // BASED ON THIS
    // https://jvm-gaming.org/t/looking-for-the-simplest-practical-javafx-game-loop/55903
    private class UpdateLoop extends AnimationTimer {

        private long before = System.nanoTime();

        @Override
        public void handle(long now) {
            float delta = (float) ((now - before) / 1E9);
            updateAndRender(delta);
            before = now;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        if (!initializeProblem())
            return;
        shuffler = new ShuffleCustomerOrderMutation();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Multi-Depot Vehicle Routing Problem Visualizer");
        Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        Group root = new Group(canvas);
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.BLACK);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        visualizer = new MDVRPVisualizer(canvas.getGraphicsContext2D());
        visualizer.calibrateVisualizer(problem);

        new UpdateLoop().start();
        primaryStage.show();
    }

    private boolean initializeProblem() {
        problem = MDVRPFiles.ReadFile("res/problems/p21");
        if (problem == null)
            return false;
        Breeder breeder = new Breeder(problem, 0);
        population = breeder.breed(5);
        return true;
    }

    private void updateAndRender(float delta) {
        // TODO: get fittest individual
        population.getIndividuals()[0] = shuffler.mutate(population.getIndividuals()[0]);
        var start = System.nanoTime();
        var schedule = RouteScheduler.scheduleRoutes(population.getIndividuals()[0], problem);

        System.out.println("Scheduling took " + (System.nanoTime() - start) + "ns");
        visualizer.drawAll(problem, schedule);
    }
}
