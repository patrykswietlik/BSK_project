package pl.bsk.project.bsk_project.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.bsk.project.bsk_project.service.PDFSignatureVerifyService;

public class PDFSignatureVerifyView extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        PDFSignatureVerifyService pdfSignatureVerifyService = new PDFSignatureVerifyService();
        Scene scene = new Scene(pdfSignatureVerifyService, 720, 576);

        primaryStage.setTitle("PDF Signature Verify");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
