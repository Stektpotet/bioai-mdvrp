package sample;

import ga.data.Initializer;
import ga.selection.ParentSelector;
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
    PopulationMDVRP population;
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

    public static void main(String[] args) {
//        launch(args);
        var problem = MDVRPFiles.ReadFile("res/problems/p01");

        Breeder breeder = new Breeder(problem, 2);
        PopulationMDVRP pop = breeder.breed(2);
        RecombinatorMDVRP recombinator = new RecombinatorMDVRP(problem);
        MutatorMDVRP mutator = new MutatorMDVRP(problem, .8f, .8f, .8f, .5f);
        ParentSelectorMDVRP parentSelector = new ParentSelectorMDVRP(2, 2, 0.8);
        SurvivorSelectorMDVRP survivorSelector = new SurvivorSelectorMDVRP();

        var individuals = pop.getIndividuals();

        recombinator.crossover(individuals.get(0), individuals.get(1));
        GeneticAlgorithm<PopulationMDVRP, ChromosomeMDVRP> geneticAlgorithm = new GeneticAlgorithm<>(breeder,
                recombinator, mutator, parentSelector, survivorSelector);

        geneticAlgorithm.run(50,  10000);

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

        new UpdateLoop().start();
        primaryStage.show();
    }

    private boolean initializeProblem() {
        problem = MDVRPFiles.ReadFile("res/problems/p21");
        if (problem == null)
            return false;
        return true;
    }

    private void updateAndRender(float delta) {
        // TODO: get fittest individual
//        visualizer.drawAll(problem, schedule);
    }
}
