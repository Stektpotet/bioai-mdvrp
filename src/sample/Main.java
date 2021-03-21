package sample;

import ga.GeneticAlgorithmRunner;
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
    MDVRP problem;
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
        // TODO: Cleanup
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        if (!initializeProblem())
            return;
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

        Breeder breeder = new Breeder(problem, 9);
        RecombinatorMDVRP recombinator = new RecombinatorMDVRP(problem);
        MutatorMDVRP mutator = new MutatorMDVRP(problem, 0.7f, 0.7f, 0.7f, 0.1f);
        ParentSelectorMDVRP parentSelector = new ParentSelectorMDVRP(4,4, 0.7);
        SurvivorSelectorMDVRP survivorSelector = new SurvivorSelectorMDVRP();

        var gaListener = new GeneticAlgorithmRunner<>(
                breeder, recombinator, mutator, parentSelector, survivorSelector, 20, 20000
        );
        gaListener.valueProperty().addListener((obs, oldChromosome, newChromosome) -> {
            if (newChromosome != null) {
                visualizer.clear();
                visualizer.drawAll(problem, newChromosome);
            }
        });
        gaListener.setOnSucceeded(event -> {
            MDVRPFiles.WriteFile(problem, gaListener.getValue(), String.format("solutions/%s.res", problem.getName()));
        });
        gaListener.start();

//        new UpdateLoop().start();d
        primaryStage.show();
    }

    private boolean initializeProblem() {
        problem = ChromosomeMDVRP.PROBLEM;
//        problem = MDVRPFiles.ReadFile("res/problems/p11");
        return true;
    }

    private void updateAndRender(float delta) {
        // TODO: get fittest individual
//        visualizer.drawAll(problem, schedule);
    }
}
