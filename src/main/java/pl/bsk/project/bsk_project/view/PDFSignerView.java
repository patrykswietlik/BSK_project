package pl.bsk.project.bsk_project.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.bsk.project.bsk_project.service.PDFSignerService;

public class PDFSignerView extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        PDFSignerService pdfSignerService = new PDFSignerService();
        Scene scene = new Scene(pdfSignerService, 720, 576);

        primaryStage.setTitle("PDF Signer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
