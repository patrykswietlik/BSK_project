package pl.bsk.project.bsk_project;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.bsk.project.bsk_project.cipher.RSACipher;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

public class PDFSignerController {

    @FXML
    private Button fileChooserButton;

    @FXML
    private Label fileLabel;

    @FXML
    protected void chooseFile() {
        Stage stage = (Stage) fileChooserButton.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (!Objects.isNull(selectedFile)) {
            fileLabel.setText("Selected: " + selectedFile.getAbsolutePath());
        }

        RSACipher rsaCipher = new RSACipher("1234");

        try {
//            rsaCipher.generateKeyPair();
//            rsaCipher.savePrivateKey("D:\\test");
//
//            PublicKey publicKey = rsaCipher.getPublicKey();
//            PrivateKey privateKey = rsaCipher.loadPrivateKey("D:\\test");
//
//            byte[] signature = PDFSigner.sign(selectedFile, privateKey);
//
//            File selectedFile2 = fileChooser.showOpenDialog(stage);
//
//            System.out.println("Document signature is valid: " + PDFSigner.verify(selectedFile2, publicKey, signature));

        } catch (Exception e) {
            System.out.println("Something went wrong");
        }

    }
}
