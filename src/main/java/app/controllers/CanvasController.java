package app.controllers;

import app.objects.Figure;
import app.objects.Point;
import app.objects.Tool;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.Stack;

public final class CanvasController extends BaseCanvasController {

    // Private Properties

    @FXML private ColorPicker colorPicker;

    private Tool selectedTool = Tool.DRAW;
    private Figure selectedFigure = Figure.LINE;

    // Drawing Logic

    void drawFigure(Point point1, Point point2) {
        var pointsToPaint = getFigurePoints(point1, point2, selectedFigure);
        paintPoints(pointsToPaint, colorPicker.getValue());
    }

    private void rowFill(Point point, Color initialColor) {
        final var pixelReader = canvas.snapshot(null, null).getPixelReader();
        final var pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();

        if(initialColor == null) {
            initialColor = pixelReader.getColor(point.x, point.y);
        }

        if(initialColor.equals(colorPicker.getValue())) {
            return;
        }

        int rightX = point.x;
        int leftX = point.x;

        int curX = point.x;
        while(curX < canvas.getWidth() && pixelReader.getColor(curX, point.y).equals(initialColor)) {
            pixelWriter.setColor(curX, point.y, colorPicker.getValue());
            rightX = curX;
            curX++;
        }

        curX = point.x - 1;
        while(curX >= 0 && pixelReader.getColor(curX, point.y).equals(initialColor)) {
            pixelWriter.setColor(curX, point.y, colorPicker.getValue());
            leftX = curX;
            curX--;
        }

        var stack = new Stack<Point>();

        if(point.y + 1 < canvas.getHeight()) {
            for(curX = leftX; curX < rightX; curX++) {
                final var curXColor = pixelReader.getColor(curX, point.y + 1);
                final var nextColor = pixelReader.getColor(curX + 1, point.y + 1);

                if(curXColor.equals(initialColor) && !nextColor.equals(initialColor)) {
                    stack.push(new Point(curX, point.y + 1));
                }

                else if(curX + 1 == rightX && nextColor.equals(initialColor)) {
                    stack.push(new Point(curX + 1, point.y + 1));
                }
            }

            if(leftX == rightX && pixelReader.getColor(rightX, point.y + 1).equals(initialColor)) {
                stack.push(new Point(rightX, point.y + 1));
            }
        }

        if(point.y - 1 >= 0) {
            for(curX = leftX; curX < rightX; curX++) {
                final var curXColor = pixelReader.getColor(curX, point.y - 1);
                final var nextColor = pixelReader.getColor(curX + 1, point.y - 1);

                if(curXColor.equals(initialColor) && !nextColor.equals(initialColor)) {
                    stack.push(new Point(curX, point.y - 1));
                }

                else if(curX + 1 == rightX && nextColor.equals(initialColor)) {
                    stack.push(new Point(curX + 1, point.y - 1));
                }
            }

            if(leftX == rightX && pixelReader.getColor(rightX, point.y - 1).equals(initialColor)) {
                stack.push(new Point(rightX, point.y - 1));
            }
        }

        while(!stack.isEmpty()) {
            rowFill(stack.pop(), initialColor);
        }
    }

    // Mouse Event Handlers

    @FXML private void onCanvasMouseClicked(MouseEvent e) {
        final var clickedX = (int) e.getX();
        final var clickedY = (int) e.getY();

        switch(selectedTool) {
            case DRAW:
                if (point1 == null) {
                    point1 = new Point(clickedX, clickedY);
                }
                else {
                    var point1 = this.point1;
                    resetSelectedPoint();
                    drawFigure(point1, new Point(clickedX, clickedY));
                }
                break;

            case FILL:
                rowFill(new Point(clickedX, clickedY), null);
                break;
        }
    }

    @FXML private void onCanvasMouseMoved(MouseEvent e) {
        if (point1 != null) {

            if (canvasSnapshot == null) {
                saveCanvasState();
            }
            else {
                restoreCanvasState();
            }

            final var movedX = (int) e.getX();
            final var movedY = (int) e.getY();
            drawFigure(point1, new Point(movedX, movedY));
        }
    }

    // Event Handlers

    @FXML private void onDrawLineClick() {
        selectedTool = Tool.DRAW;
        selectedFigure = Figure.LINE;
        resetSelectedPoint();
    }

    @FXML private void onDrawCircleClick() {
        selectedTool = Tool.DRAW;
        selectedFigure = Figure.CIRCLE;
        resetSelectedPoint();
    }

    @FXML private void onDrawEllipseClick() {
        selectedTool = Tool.DRAW;
        selectedFigure = Figure.ELLIPSE;
        resetSelectedPoint();
    }

    @FXML private void onDrawRectangleClick() {
        selectedTool = Tool.DRAW;
        selectedFigure = Figure.RECTANGLE;
        resetSelectedPoint();
    }

    @FXML private void onFillRowClick() {
        selectedTool = Tool.FILL;
        resetSelectedPoint();
    }

    @FXML private void onClearClick() {
        clearCanvas();
    }

    @FXML private void onResetSelection() {
        resetSelectedPoint();
    }

    @FXML private void onCurvesClick() {
        openController("CurvesController.fxml", "Curves", 700, 500);
    }

    @FXML private void onAboutAuthorClick() {
        openController("AboutAuthorController.fxml", "About", 500, 344);
    }

}
