package sample;

import ga.GeneticAlgorithmRunner;
import ga.GeneticAlgorithmSnapshot;
import ga.data.Chromosome;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        problem = MDVRPFiles.ReadFile("res/problems/p04");
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

        Breeder breeder = new Breeder(problem, 10);
        RecombinatorMDVRP recombinator = new RecombinatorMDVRP(problem, 1.0);
        MutatorMDVRP mutator = new MutatorMDVRP(problem, 0.7f, 1.0f, 0.7f, 0.5f);
        ParentSelectorMDVRP parentSelector = new ParentSelectorMDVRP(problem, 50,50, 0.8);
        CrowdingSelector crowdingSelector = new CrowdingSelector(problem, 10);
        MyPlusLambdaReplacement survivorSelector = new MyPlusLambdaReplacement(problem);
//        SurvivorSelectorMDVRP survivorSelector = new SurvivorSelectorMDVRP(problem);
        var gaListener = new GeneticAlgorithmRunner<>(
                breeder, recombinator, mutator, parentSelector, crowdingSelector, 1600, 20000
        );
        gaListener.valueProperty().addListener((obs, prevSnapshot, newSnapshot) -> {
            if (newSnapshot != null) {
                visualizer.clear();
                visualizer.drawAll(problem, newSnapshot);
            }
        });
        var start = System.currentTimeMillis();
        primaryStage.setOnCloseRequest(event -> {
            long diff = (System.currentTimeMillis()-start);

            System.out.println(String.format("Ended after %02d:%02d", (diff / (1000 * 60)) % 60, (diff / 1000) % 60));
            WritableImage snapshot = root.snapshot(null, null);
            MDVRPFiles.WriteImg(snapshot, String.format("solutions/%s.png", problem.getName()));
            MDVRPFiles.WriteFile(problem, gaListener.getValue().optimum, String.format("solutions/%s.res", problem.getName()));
        });

        gaListener.start();
        primaryStage.show();
    }
}
