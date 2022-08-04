package app.algorithms;

import app.objects.Point;

import java.util.ArrayList;

public interface FigureCreator {

    ArrayList<Point> calculateLine(Point point1, Point point2);
    ArrayList<Point> calculateCircle(Point point1, Point point2);
    ArrayList<Point> calculateEllipse(Point point1, Point point2);
    ArrayList<Point> calculateRectangle(Point point1, Point point2);

}
