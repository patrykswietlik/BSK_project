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
import pl.bsk.project.bsk_project.cipher.AESCipher;
import pl.bsk.project.bsk_project.cipher.KeyType;
import pl.bsk.project.bsk_project.cipher.RSACipher;
import pl.bsk.project.bsk_project.component.PDFSignature;
import pl.bsk.project.bsk_project.component.PINHandler;
import pl.bsk.project.bsk_project.utils.Util;

import java.io.File;
import java.security.PrivateKey;
import java.util.Objects;

public class PDFSignatureService extends VBox {

    private final Label pinStatusLabel = new Label();

    private final Label pdfFileLabel = new Label("Nie wybrano żadnego pliku");

    private final Label privateKeyStatusLabel = new Label("Nie wykryto pendrive do odczytu klucza prywatnego");

    private String privateKeyPath = "";

    private String pin = "";

    private File selectedPdf;

    private final Label logLabel = new Label();

    public PDFSignatureService() {
        Button chooseFileButton = new Button("Wybierz plik PDF do podpisania");
        chooseFileButton.setOnAction(this::setFile);

        PINHandler pinHandler = new PINHandler(this::setPin);

        Button signPdf = new Button("Sign");
        signPdf.setOnAction(this::signPdf);

        setAlignment(Pos.CENTER);
        setSpacing(10);
        setStyle("-fx-font-size: 16px;");


        HBox upperPanel = new HBox();
        upperPanel.setSpacing(10);
        upperPanel.setAlignment(Pos.CENTER);

        VBox leftPanel = new VBox();
        leftPanel.setStyle("-fx-border-width: 2px; -fx-border-color: black; -fx-pref-width: 300px; -fx-padding: 10px");

        VBox rightPanel = new VBox();
        rightPanel.setStyle("-fx-border-width: 2px; -fx-border-color: black; -fx-pref-width: 300px; -fx-padding: 10px");

        leftPanel.getChildren().addAll(
                pinHandler,
                pinStatusLabel);


        privateKeyStatusLabel.setWrapText(true);
        pinStatusLabel.setWrapText(true);
        logLabel.setWrapText(true);
        pdfFileLabel.setWrapText(true);

        rightPanel.getChildren().addAll(
                privateKeyStatusLabel,
                pdfFileLabel,
                chooseFileButton
        );

        upperPanel.getChildren().addAll(
                leftPanel, rightPanel
        );

        this.getChildren().addAll(
                upperPanel,
                signPdf,
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
            return;
        }

        displayLogMessage("", false);
        pdfFileLabel.setText("Wybrano: " + selectedFile.getAbsolutePath());
        selectedPdf = selectedFile;
    }

    private void setPin(String pin) {
        if (!AESCipher.pinIsValid(pin)) {
            pinStatusLabel.setText("Wyczyszczono PIN");
        } else {
            pinStatusLabel.setText("Wprowadzono PIN");
        }

        this.pin = pin;
    }

    private void signPdf(ActionEvent event) {
        if (Objects.equals(pin, "")) {
            displayLogMessage("Nie wprowadzono PINu", true);
            return;
        }
        RSACipher rsaCipher = new RSACipher(pin);
        File output = null;
        try {
            String absolutePath = selectedPdf.getAbsolutePath();
            output = new File(
                    absolutePath.replace(".pdf", "_signed.pdf")
            );
        } catch (Exception e) {
            displayLogMessage("Błędna ścieżka do pliku", true);
            return;
        }

        try {
            PrivateKey privateKey = (PrivateKey) rsaCipher.loadKeyPem(privateKeyPath, KeyType.PRIVATE_KEY);
            PDFSignature.sign(selectedPdf, output, privateKey);
        } catch (Exception e) {
            displayLogMessage("Błędny PIN", true);
            return;
        }
        displayLogMessage("Podpisano", false);
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
