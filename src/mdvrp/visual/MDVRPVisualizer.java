package mdvrp.visual;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.MatrixType;
import javafx.scene.transform.Transform;
import mdvrp.Customer;
import mdvrp.Depot;
import mdvrp.MDVRP;
import mdvrp.ga.Util;
import mdvrp.structures.Schedule;

import java.util.List;
import java.util.Map;

public class MDVRPVisualizer {
    private class DrawOptions {
        public Color[] colorCycle = {Color.GREEN, Color.FIREBRICK, Color.YELLOW, Color.DARKCYAN,
                Color.DARKSALMON, Color.PEACHPUFF, Color.AQUA, Color.MAROON};
        public Color backgroundColor = Color.valueOf("2a2e2e");
        public Color depotColor = Color.valueOf("f16");
        public Color customerColor = Color.valueOf("5ad");
        public double customerSize = 0.7;
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
        graphics.setFont(new Font(graphics.getFont().getName(), 1));
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
        graphics.fillRect(bounds.getMinX()-10, bounds.getMinY()-10, bounds.getWidth()+20, bounds.getHeight()+20);
    }
    public void drawAll(MDVRP problem, Map<Integer, Schedule> schedule) {
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
            graphics.setStroke(Color.WHITE);
            graphics.setLineWidth(0.1);
            graphics.strokeText(String.valueOf(depot.getId()),
                    depot.getX() - options.customerSize * .5,
                    depot.getY() - options.customerSize * .5 + options.depotSize, options.depotSize);
        }
    }

    public void drawRoutes(Map<Integer, Schedule> schedule, Map<Integer, Customer> customers,
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

        int x1 = start.getX(), y1 = start.getY();
        int x2 = end.getX(),   y2 = end.getY();
        graphics.strokeLine(x1, y1, x2, y2);

        int deltaX = x2 - x1;
        int deltaY = y2 - y1;

        var len = Util.duration(start, end);

        var dx = x2 - (deltaX / len) + (-deltaY / len) * 0.4f;
        var dy = y2 - (deltaY / len) + (deltaX / len) * 0.4f;
        graphics.strokeLine(x2, y2, dx, dy);

        dx = x2 - (deltaX / len) - (-deltaY / len) * 0.4f;
        dy = y2 - (deltaY / len) - (deltaX / len) * 0.4f;
        graphics.strokeLine(x2, y2, dx, dy);
    }
}
