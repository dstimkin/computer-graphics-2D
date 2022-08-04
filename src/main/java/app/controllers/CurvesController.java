package app.controllers;

import app.objects.Curve;
import app.objects.Figure;
import app.objects.Point;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class CurvesController extends BaseCanvasController {

    private ArrayList<Point> pivotPoints = new ArrayList<>();
    private Point druggingPoint;

    private Curve currentCurve = Curve.BSPLINE;
    private Boolean shouldDrawPivotLines = true;
    private Boolean shouldCloseCurve = false;

    // Drawing Logic

    private void redrawCanvas() {
        clearCanvas();

        paintPivotPoints();

        if(shouldDrawPivotLines)
            paintPivotLines();

        switch(currentCurve) {
            case BSPLINE:
                paintBSplines();
                break;

            case SIMPLEBEZIER:
                paintSimpleBezierCurve();
                break;

            case COMPOSITEBEZIER:
                paintComposedBezierCurve();
                break;

            case CASTELJAU:
                paintCasteljauBezierCurve();
                break;
        }
    }

    private void paintPivotPoints() {
        if(pivotPoints.size() == 0)
            return;

        var gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GREEN);
        gc.fillRect(pivotPoints.get(0).x - 7, pivotPoints.get(0).y - 7, 14, 14);
        var counter = 0;
        for(int i = 1; i < pivotPoints.size(); i++) {
            if(counter < 2) {
                gc.setFill(Color.AQUAMARINE);
                counter++;
            }
            else if(counter < 4) {
                gc.setFill(Color.GREEN);
                counter++;
            }
            else {
                counter = 0;
                i--;
                continue;
            }

            gc.fillRect(pivotPoints.get(i).x - 7, pivotPoints.get(i).y - 7, 14, 14);
        }
    }

    private void paintPivotLines() {
        var pointsToDraw = new ArrayList<Point>();
        for(var i = 0; i < pivotPoints.size() - 1; i++) {
            var point1 = pivotPoints.get(i);
            var point2 = pivotPoints.get(i + 1);
            pointsToDraw.addAll(getFigurePoints(point1, point2, Figure.LINE));
        }

        if(shouldCloseCurve && pivotPoints.size() > 3)
            pointsToDraw.addAll(getFigurePoints(pivotPoints.get(0),
                    pivotPoints.get(pivotPoints.size() - 1),
                    Figure.LINE));

        paintPoints(pointsToDraw, Color.ORANGE);
    }

    private void paintBSplines() {
        if(pivotPoints.size() < 4)
            return;

        var pointsToDraw = new ArrayList<Point>();
        for(var i = 0; i < pivotPoints.size() - 3; i++)
            pointsToDraw.addAll(calculateBSplines(pivotPoints.get(i),
                    pivotPoints.get(i + 1),
                    pivotPoints.get(i + 2),
                    pivotPoints.get(i + 3)));

        if(shouldCloseCurve) {
            pointsToDraw.addAll(calculateBSplines(pivotPoints.get(pivotPoints.size() - 3),
                    pivotPoints.get(pivotPoints.size() - 2),
                    pivotPoints.get(pivotPoints.size() - 1),
                    pivotPoints.get(0)));
            pointsToDraw.addAll(calculateBSplines(pivotPoints.get(pivotPoints.size() - 2),
                    pivotPoints.get(pivotPoints.size() - 1),
                    pivotPoints.get(0),
                    pivotPoints.get(1)));
            pointsToDraw.addAll(calculateBSplines(pivotPoints.get(pivotPoints.size() - 1),
                    pivotPoints.get(0),
                    pivotPoints.get(1),
                    pivotPoints.get(2)));
        }

        paintPoints(pointsToDraw, Color.BLACK);
    }

    public ArrayList<Point> calculateBSplines(Point p1, Point p2, Point p3, Point p4) {
        var pointsToDraw = new ArrayList<Point>();

        for(double t = 0.0; t <= 1; t += 0.001) {
            var b1 = Math.pow(1 - t, 3);
            var b2 = 3 * Math.pow(t, 3) - 6 * Math.pow(t, 2) + 4;
            var b3 = -3 * Math.pow(t, 3) + 3 * Math.pow(t, 2) + 3 * t + 1;
            var b4 = Math.pow(t, 3);

            var x = (b1 * p1.x + b2 * p2.x + b3 * p3.x + b4 * p4.x) / 6;
            var y = (b1 * p1.y + b2 * p2.y + b3 * p3.y + b4 * p4.y) / 6;
            pointsToDraw.add(new Point((int) x, (int) y));
        }

        return pointsToDraw;
    }

    private void paintSimpleBezierCurve() {
        if(pivotPoints.size() < 2)
            return;

        var pivotPointsCopy = (ArrayList<Point>) pivotPoints.clone();
        if(shouldCloseCurve)
            pivotPointsCopy.add(pivotPointsCopy.get(0));

        paintPoints(calculateBezierCurve(pivotPointsCopy), Color.BLACK);
    }

    private void paintComposedBezierCurve() {
        if(pivotPoints.size() < 4)
            return;

        var pointsToDraw = new ArrayList<Point>();
        var pivotPointsCopy = (ArrayList<Point>) pivotPoints.clone();

        if(shouldCloseCurve && pivotPoints.size() % 2 == 0) {
            pivotPointsCopy.add(pivotPoints.get(0));
            pivotPointsCopy.add(pivotPoints.get(0));
        }
        else if(shouldCloseCurve)
            pivotPointsCopy.add(pivotPoints.get(0));

        for(int i = 0; pivotPointsCopy.size() > i + 2; i += 2) {
            ArrayList<Point> tempPointsArray;
            if(i == 0)
                tempPointsArray = new ArrayList<>(pivotPointsCopy.subList(i, i + 3));
            else {
                tempPointsArray = new ArrayList<>();
                var midX = (pivotPointsCopy.get(i - 1).x + pivotPointsCopy.get(i).x) / 2;
                var midY = (pivotPointsCopy.get(i - 1).y + pivotPointsCopy.get(i).y) / 2;
                tempPointsArray.add(new Point(midX, midY));
                tempPointsArray.add(pivotPointsCopy.get(i));
                tempPointsArray.add(pivotPointsCopy.get(i + 1));
            }

            if(pivotPointsCopy.size() < 6)
                tempPointsArray.add(pivotPointsCopy.get(i + 3));
            else if(i == 0) {
                var midX = (pivotPointsCopy.get(i + 2).x + pivotPointsCopy.get(i + 3).x) / 2;
                var midY = (pivotPointsCopy.get(i + 2).y + pivotPointsCopy.get(i + 3).y) / 2;
                tempPointsArray.add(new Point(midX, midY));
            }
            else if(i + 4 >= pivotPointsCopy.size())
                tempPointsArray.add(pivotPointsCopy.get(i + 2));
            else {
                var midX = (pivotPointsCopy.get(i + 1).x + pivotPointsCopy.get(i + 2).x) / 2;
                var midY = (pivotPointsCopy.get(i + 1).y + pivotPointsCopy.get(i + 2).y) / 2;
                tempPointsArray.add(new Point(midX, midY));
            }

            if(i == 0)
                i++;

            pointsToDraw.addAll(calculateBezierCurve(tempPointsArray));
        }

        paintPoints(pointsToDraw, Color.BLACK);
    }

    private ArrayList<Point> calculateBezierCurve(ArrayList<Point> points) {
        var pointsToDraw = new ArrayList<Point>();

        for(double t = 0.0; t <= 1; t += 0.0001) {
            var sumX = 0;
            var sumY = 0;

            for(int i = 0; i < points.size(); i++) {
                var n = points.size() - 1;
                var coef = factorial(n) / (factorial(i) * factorial(n - i)) * Math.pow(t, i) * Math.pow(1 - t, n - i);
                sumX += points.get(i).x * coef;
                sumY += points.get(i).y * coef;
            }

            pointsToDraw.add(new Point(sumX, sumY));
        }

        return pointsToDraw;
    }

    private long factorial(int n) {
        long factorial = 1;
        for (int i = 2; i <= n; i++)
            factorial = factorial * i;
        return factorial;
    }

    private void paintCasteljauBezierCurve() {
        var pivotPointsCopy = (ArrayList<Point>) pivotPoints.clone();
        if(shouldCloseCurve)
            pivotPointsCopy.add(pivotPointsCopy.get(0));

        var pointsToDraw = new ArrayList<Point>();
        for (double t = 0; t <= 1; t += 0.0001)
            pointsToDraw.add(calculateCasteljauPoint(pivotPointsCopy.size() - 1, 0, t, pivotPointsCopy));

        paintPoints(pointsToDraw, Color.BLACK);
    }

    private Point calculateCasteljauPoint(int recursionDepth, int i, double t, ArrayList<Point> points) {
        if(recursionDepth == 0)
            return points.get(i);

        Point p1 = calculateCasteljauPoint(recursionDepth - 1, i, t, points);
        Point p2 = calculateCasteljauPoint(recursionDepth - 1, i + 1, t, points);

        var x = (int) ((1 - t) * p1.x + t * p2.x);
        var y = (int) ((1 - t) * p1.y + t * p2.y);
        return new Point(x, y);
    }

    // Mouse Event Handlers

    @FXML
    private void onCanvasMouseClicked(MouseEvent e) {
        if(druggingPoint != null) {
            druggingPoint = null;
            return;
        }

        var clickedPoint = new Point((int) e.getX(), (int) e.getY());
        pivotPoints.add(clickedPoint);
        redrawCanvas();
    }

    @FXML private void onCanvasMouseDrugged(MouseEvent e) {
        var currentPoint = new Point((int) e.getX(), (int) e.getY());

        if(currentPoint.x > canvas.getWidth() ||
                currentPoint.x < 0 ||
                currentPoint.y > canvas.getHeight() ||
                currentPoint.y < 0) {
            return;
        }

        if(druggingPoint == null)
            druggingPoint = nearestPivotPoint(currentPoint);

        if(druggingPoint == null)
            return;

        pivotPoints.set(pivotPoints.indexOf(druggingPoint), currentPoint);
        druggingPoint = currentPoint;
        redrawCanvas();
    }

    private Point nearestPivotPoint(Point point) {
        for(var pivotPoint: pivotPoints)
            if (Math.abs(point.x - pivotPoint.x) <= 10 && Math.abs(point.y - pivotPoint.y) <= 10)
                return pivotPoint;

        return null;
    }

    // Event Handlers

    @FXML private void onDrawBSpline() {
        currentCurve = Curve.BSPLINE;
        redrawCanvas();
    }

    @FXML private void onDrawSimpleBezier() {
        currentCurve = Curve.SIMPLEBEZIER;
        redrawCanvas();
    }

    @FXML private void onDrawCompositeBezier() {
        currentCurve = Curve.COMPOSITEBEZIER;
        redrawCanvas();
    }

    @FXML private void onDrawCasteljau() {
        currentCurve = Curve.CASTELJAU;
        redrawCanvas();
    }

    @FXML private void onDrawPivotLines() {
        shouldDrawPivotLines = !shouldDrawPivotLines;
        redrawCanvas();
    }

    @FXML private void onCloseCurve() {
        shouldCloseCurve = !shouldCloseCurve;
        redrawCanvas();
    }

    @FXML private void onClear() {
        pivotPoints = new ArrayList<>();
        redrawCanvas();
    }

    @FXML private void onDeleteLastPoint() {
        if (pivotPoints.size() > 0)
            pivotPoints.remove(pivotPoints.size() - 1);

        redrawCanvas();
    }

}
