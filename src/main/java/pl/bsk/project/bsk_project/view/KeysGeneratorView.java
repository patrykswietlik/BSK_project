package pl.bsk.project.bsk_project.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.bsk.project.bsk_project.service.KeysGeneratorService;

public class KeysGeneratorView extends Application {

    @Override
    public void start(Stage primaryStage) {
        KeysGeneratorService keysGeneratorService = new KeysGeneratorService();
        Scene scene = new Scene(keysGeneratorService, 300, 500);

        primaryStage.setTitle("Key Generator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
