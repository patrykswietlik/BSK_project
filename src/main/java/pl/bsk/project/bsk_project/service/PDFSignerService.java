package pl.bsk.project.bsk_project.service;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.bsk.project.bsk_project.cipher.AESCipher;
import pl.bsk.project.bsk_project.cipher.KeyType;
import pl.bsk.project.bsk_project.cipher.RSACipher;
import pl.bsk.project.bsk_project.component.PDFSigner;
import pl.bsk.project.bsk_project.component.PINHandler;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.security.PrivateKey;
import java.util.Objects;

public class PDFSignerService extends VBox {

    private final Label pinStatusLabel = new Label();

    private final Label pdfFileLabel = new Label("Nie wybrano żadnego pliku");

    private final Label privateKeyStatusLabel = new Label("Nie wykryto pendrive do odczytu klucza prywatnego");

    private String privateKeyPath = "";

    private String pin = "";

    private File selectedPdf;

    public PDFSignerService() {
        Button chooseFileButton = new Button("Wybierz plik PDF do podpisania");
        chooseFileButton.setOnAction(this::setFile);

        PINHandler pinHandler = new PINHandler(this::setPin);

        this.getChildren().addAll(
                pinHandler,
                pinStatusLabel,
                pdfFileLabel,
                privateKeyStatusLabel,
                chooseFileButton
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
            return;
        }

        pdfFileLabel.setText("Wybrano: " + selectedFile.getAbsolutePath());
        selectedPdf = selectedFile;
    }

    private void setPin(String pin) {
        if (!AESCipher.pinIsValid(pin)) {
            pinStatusLabel.setText("Wprowadzono błędny pin");
        } else {
            pinStatusLabel.setText("Pin został wprowadzony poprawnie");
        }

        this.pin = pin;
    }

    private void signPdf(ActionEvent event) {
        RSACipher rsaCipher = new RSACipher(pin);
        try {
            PrivateKey privateKey = (PrivateKey) rsaCipher.loadKey(privateKeyPath, KeyType.PRIVATE_KEY);
            byte[] signature = PDFSigner.sign(selectedPdf, privateKey);
        } catch (Exception e) {
            return;
        }
    }


    private void handleUSBThread() {
        Thread thread = new Thread(() -> {
            while (true) {
                String optionalPath = getUSBPath();

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
}
