package pl.bsk.project.bsk_project.service;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import pl.bsk.project.bsk_project.cipher.AESCipher;
import pl.bsk.project.bsk_project.cipher.KeyType;
import pl.bsk.project.bsk_project.cipher.RSACipher;
import pl.bsk.project.bsk_project.component.PINHandler;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public class KeysGeneratorService extends VBox {

    private final Label pinStatusLabel = new Label();

    private final Label privateKeyStatusLabel = new Label("Nie wykryto pendrive do zapisu klucza prywatnego");

    private final Label publicKeyStatusLabel = new Label("Nie wybrano lokalizacji do zapisu klucza publicznego");

    private final Label logLabel = new Label();

    private final Button generateKeysButton = new Button("Wygeneruj parę kluczy");

    private String pin = "";

    private String privateKeyPath = "";

    private String publicKeyPath = "";

    public KeysGeneratorService() {
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

        PINHandler pinHandler = new PINHandler(this::setPin);

        leftPanel.getChildren().addAll(
                pinHandler,
                pinStatusLabel);

        Button setPublicKeyLocationButton = new Button("Wybierz miejsce zapisu klucza publicznego");
        setPublicKeyLocationButton.setOnAction(this::setPublicKeyPath);

        generateKeysButton.setDisable(true);
        generateKeysButton.setOnAction(this::generateKeys);

        privateKeyStatusLabel.setWrapText(true);
        publicKeyStatusLabel.setWrapText(true);
        pinStatusLabel.setWrapText(true);
        logLabel.setWrapText(true);
        generateKeysButton.setWrapText(true);
        setPublicKeyLocationButton.setWrapText(true);

        rightPanel.getChildren().addAll(
                privateKeyStatusLabel,
                publicKeyStatusLabel,
                setPublicKeyLocationButton
        );

        upperPanel.getChildren().addAll(
                leftPanel, rightPanel
        );

        this.getChildren().addAll(
                upperPanel,
                generateKeysButton,
                logLabel
        );

        handleUSBThread();
    }

    private void setPublicKeyPath(ActionEvent event) {
        Stage stage = (Stage) this.getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose directory");

        File selectedDirectory = directoryChooser.showDialog(stage);

        if (Objects.isNull(selectedDirectory)) {
            displayLogMessage("Nie udało się wybrać ścieżki do zapisu klucza publicznego", true);
            return;
        }

        displayLogMessage("", false);
        publicKeyStatusLabel.setText("Wybrano ścieżkę do zapisu klucza publicznego: " + selectedDirectory.toPath());
        publicKeyPath = selectedDirectory.toPath().toString();
    }

    private void setPin(String pin) {
        if (!AESCipher.pinIsValid(pin)) {
            pinStatusLabel.setText("Wprowadzono błędny pin");
        } else {
            pinStatusLabel.setText("Pin został wprowadzony poprawnie");
        }

        this.pin = pin;
    }

    private void generateKeys(ActionEvent event) {
        if (!generatorParametersIsValid()) {
            displayLogMessage("Nie udało się wygenerować kluczy", true);
            return;
        }

        RSACipher rsaCipher = new RSACipher(pin);
        Path privateKeySavePath = Path.of(privateKeyPath);
        Path publicKeySavePath = Path.of(privateKeyPath);

        try {
            rsaCipher.generateKeyPair();
            rsaCipher.saveKeyPem(privateKeySavePath.toString(), KeyType.PRIVATE_KEY);
            rsaCipher.saveKeyPem(publicKeySavePath.toString(), KeyType.PUBLIC_KEY);

            displayLogMessage("Para kluczy została wygenerowana", false);
        } catch (Exception e) {
            displayLogMessage("Wystąpił błąd", true);
            e.printStackTrace();
        }

    }

    private void handleUSBThread() {
        Thread thread = new Thread(() -> {
            while (true) {
                String optionalPath = getUSBPath();

                if (!Objects.isNull(optionalPath)) {
                    Platform.runLater(() -> {
                        privateKeyStatusLabel.setText("Wykryto pendrive do zapisu klucza prywatnego");
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

                Platform.runLater(this::generatorParametersIsValid);
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    private String getUSBPath() {
        File[] roots = File.listRoots();
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();

        for (File root : roots) {
            if (fileSystemView.getSystemDisplayName(root).contains("USB")) {
                return root.getPath();
            }
        }

        return null;
    }

    private void displayLogMessage(String message, boolean isError) {
        logLabel.setStyle("-fx-text-fill: " + (isError ? "red" : "green"));
        logLabel.setText(message);
    }

    private boolean generatorParametersIsValid() {
        boolean isValid = AESCipher.pinIsValid(pin) &&  !publicKeyPath.isBlank() && !privateKeyPath.isBlank();
        generateKeysButton.setDisable(!isValid);
        return isValid;
    }

    @FXML
    public void initialize() {

    }

}
