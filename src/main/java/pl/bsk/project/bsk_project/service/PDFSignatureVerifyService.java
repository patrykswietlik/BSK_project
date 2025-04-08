package pl.bsk.project.bsk_project.service;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.bsk.project.bsk_project.cipher.KeyType;
import pl.bsk.project.bsk_project.cipher.RSACipher;
import pl.bsk.project.bsk_project.component.PDFSignature;
import pl.bsk.project.bsk_project.utils.Util;

import java.io.File;
import java.security.PublicKey;
import java.util.Objects;

public class PDFSignatureVerifyService extends VBox {

    private final Label pdfFileLabel = new Label("Nie wybrano żadnego pliku");

    private final Label privateKeyStatusLabel = new Label("Nie wykryto pendrive do odczytu klucza publicznego");

    private final Label verificationResultLabel = new Label();

    private String privateKeyPath = "";

    private File selectedPdf;

    private final Label logLabel = new Label();

    public PDFSignatureVerifyService() {
        Button chooseFileButton = new Button("Wybierz plik PDF do sprawdzenia podpisu");
        chooseFileButton.setOnAction(this::setFile);

        Button signPdf = new Button("Check");
        signPdf.setOnAction(this::verifySignature);

        setAlignment(Pos.CENTER);
        setSpacing(10);
        setStyle("-fx-font-size: 16px;");

        HBox upperPanel = new HBox();
        upperPanel.setSpacing(10);
        upperPanel.setAlignment(Pos.CENTER);

        VBox innerPanel = new VBox();
        innerPanel.setSpacing(10);
        innerPanel.setStyle("-fx-border-width: 2px; -fx-border-color: black; -fx-pref-width: 400px; -fx-padding: 10px");

        privateKeyStatusLabel.setWrapText(true);
        logLabel.setWrapText(true);
        pdfFileLabel.setWrapText(true);

        innerPanel.getChildren().addAll(
                pdfFileLabel,
                privateKeyStatusLabel,
                chooseFileButton,
                signPdf,
                verificationResultLabel
        );
        upperPanel.getChildren().addAll(
                innerPanel
        );
        this.getChildren().addAll(
                upperPanel,
                logLabel
        );

        handleUSBThread();
    }

    public void setFile(ActionEvent event) {
        Stage stage = (Stage) getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (Objects.isNull(selectedFile)) {
            displayLogMessage("Nie udało się wybrać pliku", true);
            verificationResultLabel.setText("");
            return;
        }

        displayLogMessage("", false);
        pdfFileLabel.setText("Wybrano: " + selectedFile.getAbsolutePath());
        selectedPdf = selectedFile;
        verificationResultLabel.setText("");
    }

    private void verifySignature(ActionEvent event) {
        RSACipher rsaCipher = new RSACipher("1234");

        try {
            PublicKey publicKey = (PublicKey) rsaCipher.loadKeyPem(privateKeyPath, KeyType.PUBLIC_KEY);
            boolean signatureIsValid = PDFSignature.verify(selectedPdf, publicKey);

            verificationResultLabel.setText("Podpis jest " + (signatureIsValid ? "POPRAWNY" : "NIEPOPRWANY"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void handleUSBThread() {
        Thread thread = new Thread(() -> {
            while (true) {
                String optionalPath = Util.getUSBPath();

                if (!Objects.isNull(optionalPath)) {
                    Platform.runLater(() -> {
                        privateKeyStatusLabel.setText("Wykryto pendrive do odczytu klucza prywatnego");
                        privateKeyPath = optionalPath;
                    });
                } else {
                    Platform.runLater(() -> {
                        privateKeyStatusLabel.setText("Nie wykryto pendrive do zapisu klucza prywatnego");
                        privateKeyPath = "";
                    });
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    private void displayLogMessage(String message, boolean isError) {
        logLabel.setStyle("-fx-text-fill: " + (isError ? "red" : "green"));
        logLabel.setText(message);
    }
}
