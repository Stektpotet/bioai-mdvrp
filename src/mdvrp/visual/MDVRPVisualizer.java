package mdvrp.visual;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.scene.transform.MatrixType;
import mdvrp.Customer;
import mdvrp.Depot;
import mdvrp.MDVRP;

import java.util.List;
import java.util.Map;

public class MDVRPVisualizer {
    private class DrawOptions {
        public Color[] colorCycle = {Color.GREEN, Color.FIREBRICK, Color.YELLOW, Color.DARKCYAN,
                Color.DARKSALMON, Color.PEACHPUFF, Color.AQUA, Color.MAROON};
        public Color backgroundColor = Color.valueOf("2a2e2e");
        public Color depotColor = Color.valueOf("f16");
        public Color customerColor = Color.valueOf("5ad");
        public double customerSize = 1;
        public double depotSize = 1;
    }

    private double width, height;
    private final GraphicsContext graphics;
    private Affine transform;
    private BoundingBox bounds;
    private final DrawOptions options;


    public MDVRPVisualizer(GraphicsContext graphics) {
        this.graphics = graphics;
        options = new DrawOptions();
        width = graphics.getCanvas().getWidth();
        height = graphics.getCanvas().getHeight();
    }
    public void calibrateVisualizer(MDVRP problem) {
        Canvas canvas = graphics.getCanvas();
        width = canvas.getWidth();
        height = canvas.getHeight();

        bounds = calculateProblemBounds(problem);
        transform = new Affine();
        double largestAxis = Math.max(bounds.getWidth(), bounds.getHeight());
        transform.appendScale(width / largestAxis, height / largestAxis);
        transform.appendTranslation(
                -bounds.getMinX() + (largestAxis - bounds.getWidth()) / 2,
                -bounds.getMinY() + (largestAxis - bounds.getHeight()) / 2);
        graphics.setTransform(transform);
    }

    public void clear() {
        graphics.setFill(options.backgroundColor);
        graphics.fillRect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
    }
    public void drawAll(MDVRP problem, Map<Integer, List<List<Integer>>> schedule) {
        clear();
        drawRoutes(schedule, problem.getCustomers(), problem.getDepots());
        drawProblem(problem);
    }

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

    public void drawProblem(MDVRP problem) {
        graphics.setFill(options.customerColor);
        for (var customer : problem.getCustomers().values()) {
            graphics.fillOval(
                    customer.getX() - options.customerSize * .5,
                    customer.getY() - options.customerSize * .5,
                    options.customerSize, options.customerSize);
        }
        graphics.setFill(options.depotColor);
        for (var depot : problem.getDepots().values()) {
            graphics.fillRect(
                    depot.getX() - options.customerSize * .5,
                    depot.getY() - options.customerSize * .5,
                    options.depotSize, options.depotSize);
        }
    }

    public void drawRoutes(Map<Integer, List<List<Integer>>> schedule, Map<Integer, Customer> customers,
                            Map<Integer, Depot> depots) {
        for (var routesPerDepot : schedule.entrySet()) {
            Depot depot = depots.get(routesPerDepot.getKey());
            int colorCycler = 0;
            for (var route : routesPerDepot.getValue()) {
                Customer current = depot;
                graphics.setStroke(options.colorCycle[colorCycler]);
                for (var nextCustomerID : route) {
                    Customer next = customers.get(nextCustomerID);
                    drawLine(current, next);
                    current = next;
                }
                drawLine(current, depot);
                colorCycler = (colorCycler + 1) % options.colorCycle.length;
            }
        }
    }

    private void drawLine(Customer start, Customer end) {
        graphics.setLineWidth(0.25);
        graphics.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
    }
}
