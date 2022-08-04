module app.bresenhams_algorithms {
    requires javafx.controls;
    requires javafx.fxml;


    opens app to javafx.fxml;
    exports app;
    exports app.controllers;
    opens app.controllers to javafx.fxml;
    exports app.objects;
    opens app.objects to javafx.fxml;
    exports app.algorithms;
    opens app.algorithms to javafx.fxml;
}