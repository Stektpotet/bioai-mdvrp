package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

public class Main extends Application {

    static final Paint DEPOT_COLOR = Paint.valueOf("f16");
    static final Paint CUSTOMER_COLOR = Paint.valueOf("5ad");

    @Override
    public void start(Stage primaryStage) throws Exception{
        MDVRP problem = MDVRPFiles.ReadFile("res/problems/p23");
        if (problem == null)
            return;

        // TODO: I think the our GA should be running using Platform.runLater()
        // TODO: tutorials.jenkov.com/javafx/concurrency.html

        List<Node> nodes = new ArrayList<Node>();

        for (Customer c : problem.getCustomers()) {
            Circle node = new Circle(c.getX(), c.getY(),1, CUSTOMER_COLOR);
            nodes.add(node);
            System.out.println(c);
        }
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

        primaryStage.setTitle("Multi-Depot Vehicle Routing Problem Visualizer");
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
