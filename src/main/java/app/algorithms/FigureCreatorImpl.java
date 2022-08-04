package app.algorithms;

import app.objects.Point;

import java.util.ArrayList;

public class FigureCreatorImpl implements FigureCreator {

    public ArrayList<Point> calculateLine(Point point1, Point point2) {
        final var dx = Math.abs(point2.x - point1.x);
        final var dy = Math.abs(point2.y - point1.y);

        var errorX = 2 * dx - dy;
        final var delta_exS = 2 * dx;
        final var delta_exD = 2 * dx - 2 * dy;

        var errorY = 2 * dy - dx;
        final var delta_eyS = 2 * dy;
        final var delta_eyD = 2 * dy - 2 * dx;

        final var signX = Integer.compare(point2.x - point1.x, 0);
        final var signY = Integer.compare(point2.y - point1.y, 0);

        final var linePixels = new ArrayList<Point>();
        var currentX = point1.x;
        var currentY = point1.y;

        do {
            linePixels.add(new Point(currentX, currentY));

            if (errorX > 0) {
                currentX += signX;
                errorX += delta_exD;
            } else {
                errorX += delta_exS;
            }

            if (errorY > 0) {
                currentY += signY;
                errorY += delta_eyD;
            } else {
                errorY += delta_eyS;
            }

        } while (currentX != point2.x || currentY != point2.y);

        linePixels.add(new Point(currentX, currentY));
        return  linePixels;
    }

    public ArrayList<Point> calculateCircle(Point point1, Point point2) {
        final var radius = (int) Math.sqrt((point1.x - point2.x) * (point1.x - point2.x)
                + (point1.y - point2.y) * (point1.y - point2.y));

        var x = 0;
        var y = radius;

        final var circlePixels = new ArrayList<Point>();
        var delta = 2 - 2 * radius;

        while(y >= 0) {
            circlePixels.add(new Point(point1.x + x, point1.y + y));
            circlePixels.add(new Point(point1.x + x, point1.y - y));
            circlePixels.add(new Point(point1.x - x, point1.y + y));
            circlePixels.add(new Point(point1.x - x, point1.y - y));

            var error = 2 * (delta + y) - 1;
            if (delta < 0 && error <= 0) { // right step
                x++;
                delta += 2 * x + 1;
                continue;
            }

            error = 2 * (delta - x) - 1;
            if (delta > 0 && error > 0) { // down step
                y--;
                delta += 1 - 2 * y;
                continue;
            }

            // diagonal step
            x++;
            y--;
            delta += 2 * (x - y) + 2;
        }

        return circlePixels;
    }

    public ArrayList<Point> calculateEllipse(Point point1, Point point2) {
        int x0 = point1.x;
        int y0 = point1.y;
        int x1 = point2.x;
        int y1 = point2.y;

        int a = Math.abs(x1 - x0), b = Math.abs(y1 - y0), b1 = b&1;
        long dx = 4 * (1 - a) * b * b, dy = 4 * (b1 + 1) * a * a;
        long err = dx + dy + b1 * a * a, e2;

        if (x0 > x1) {
            x0 = x1;
            x1 += a;
        }

        if (y0 > y1)
            y0 = y1;

        y0 += (b + 1) / 2;
        y1 = y0 - b1;
        a *= 8 * a;
        b1 = 8 * b * b;

        final var ellipsePixels = new ArrayList<Point>();

        do {
            ellipsePixels.add(new Point(x1, y0));
            ellipsePixels.add(new Point(x0, y0));
            ellipsePixels.add(new Point(x0, y1));
            ellipsePixels.add(new Point(x1, y1));

            e2 = 2 * err;
            if (e2 <= dy) {
                y0++;
                y1--;
                err += dy += a;
            }

            if (e2 >= dx || 2 * err > dy) {
                x0++;
                x1--;
                err += dx += b1;
            }

        } while (x0 <= x1);

        while (y0 - y1 < b) {
            ellipsePixels.add(new Point(x0 - 1, y0));
            ellipsePixels.add(new Point(x1 + 1, y0++));
            ellipsePixels.add(new Point(x0 - 1, y1));
            ellipsePixels.add(new Point(x1 + 1, y1--));
        }

        return ellipsePixels;
    }

    public ArrayList<Point> calculateRectangle(Point point1, Point point2) {
        final var x1 = Integer.min(point1.x, point2.x);
        final var x2 = Integer.max(point1.x, point2.x);
        final var y1 = Integer.min(point1.y, point2.y);
        final var y2 = Integer.max(point1.y, point2.y);

        final var rectanglePixels = new ArrayList<Point>();

        for(int i = x1; i <= x2; i++) {
            rectanglePixels.add(new Point(i, y1));
            rectanglePixels.add(new Point(i, y2));
        }

        for(int i = y1 + 1; i < y2; i++) {
            rectanglePixels.add(new Point(x1, i));
            rectanglePixels.add(new Point(x2, i));
        }

        final var lastPoint = new Point(x2, y2);
        rectanglePixels.remove(lastPoint);
        rectanglePixels.add(lastPoint);

        return rectanglePixels;
    }

}