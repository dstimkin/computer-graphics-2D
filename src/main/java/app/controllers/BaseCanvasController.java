package app.controllers;

import app.objects.Figure;
import app.algorithms.FigureCreator;
import app.algorithms.FigureCreatorImpl;
import app.objects.Point;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class BaseCanvasController {

    @FXML Canvas canvas;

    Point point1;
    WritableImage canvasSnapshot;

    private final FigureCreator figureCreator = new FigureCreatorImpl();
    private final double scaleFactorX = Screen.getPrimary().getOutputScaleX();
    private final double scaleFactorY = Screen.getPrimary().getOutputScaleY();

    // Drawing Logic

    ArrayList<Point> getFigurePoints(Point point1, Point point2, Figure figure) {
        final ArrayList<Point> pointsList;

        switch(figure) {
            case LINE:
                pointsList = figureCreator.calculateLine(point1, point2);
                break;

            case CIRCLE:
                pointsList = figureCreator.calculateCircle(point1, point2);
                break;

            case ELLIPSE:
                pointsList = figureCreator.calculateEllipse(point1, point2);
                break;

            case RECTANGLE:
                pointsList = figureCreator.calculateRectangle(point1, point2);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + figure);
        }

        return pointsList;
    }

    void paintPoints(ArrayList<Point> pointsList, Color color) {
        final var pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
        for (var point : pointsList) {
            pixelWriter.setColor(point.x, point.y, color);
        }
    }

    // Canvas Controls

    void clearCanvas() {
        final var gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvasSnapshot = null;
        resetSelectedPoint();
    }

    void resetSelectedPoint() {
        point1 = null;
        if (canvasSnapshot != null) {
            restoreCanvasState();
            canvasSnapshot = null;
        }
    }

    void saveCanvasState() {
        final var writableImage = new WritableImage((int) (scaleFactorX * canvas.getWidth()),
                (int) (scaleFactorY * canvas.getHeight()));
        final var params = new SnapshotParameters();
        params.setTransform(Transform.scale(scaleFactorX, scaleFactorY));
        params.setFill(Color.TRANSPARENT);
        canvasSnapshot = canvas.snapshot(params, writableImage);
    }

    void restoreCanvasState() {
        final var gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(canvasSnapshot,
                0,
                0,
                canvas.getWidth() * scaleFactorX,
                canvas.getHeight() * scaleFactorY,
                0,
                0,
                canvas.getWidth(),
                canvas.getHeight());
    }

    void openController(String name, String title, int width, int height) {
        try {
            final var fxmlLoader = new FXMLLoader(getClass().getResource(name));
            final var scene = new Scene(fxmlLoader.load(), width, height);
            final var stage = new Stage();

            stage.setTitle(title);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            System.out.println("Whoops, something went wrong");
        }
    }

}
