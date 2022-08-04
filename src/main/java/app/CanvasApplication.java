package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CanvasApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        final var fxmlLoader = new FXMLLoader(CanvasApplication.class.getResource("controllers/CanvasController.fxml"));
        final var scene = new Scene(fxmlLoader.load(), 700, 500);

        stage.setTitle("Canvas");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}