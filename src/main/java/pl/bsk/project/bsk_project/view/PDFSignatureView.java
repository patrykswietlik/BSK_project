package pl.bsk.project.bsk_project.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.bsk.project.bsk_project.service.PDFSignatureService;

public class PDFSignatureView extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        PDFSignatureService pdfSignatureService = new PDFSignatureService();
        Scene scene = new Scene(pdfSignatureService, 720, 576);

        primaryStage.setTitle("PDF Signature");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
