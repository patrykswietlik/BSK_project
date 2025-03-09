package pl.bsk.project.bsk_project.service;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import pl.bsk.project.bsk_project.cipher.KeyType;
import pl.bsk.project.bsk_project.cipher.RSACipher;
import pl.bsk.project.bsk_project.component.PINHandler;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public class KeysGeneratorService extends VBox {

    private final Label privateKeyPath = new Label();

    private final Label publicKeyPath = new Label();

    public KeysGeneratorService() {
        setAlignment(Pos.CENTER);
        setSpacing(10);
        setStyle("-fx-font-size: 16px;");

        PINHandler pinHandler = new PINHandler(this::submitPin);

        VBox vBox = new VBox();
        vBox.setSpacing(10.0);
        vBox.setStyle("-fx-padding: 20px 0 0 0");
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(
                new Label("Ścieżka do klucza prywatnego: "),
                privateKeyPath,
                new Label("Ścieżka do klucza publicznego: "),
                publicKeyPath
        );

        this.getChildren().addAll(
                pinHandler,
                vBox
        );
    }

    private Path getPath() {
        Stage stage = (Stage) this.getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose directory");

        File selectedDirectory = directoryChooser.showDialog(stage);

        if (Objects.isNull(selectedDirectory)) {
            System.out.println("Failed");
            return null;
        }

        System.out.println(selectedDirectory.toPath());

        return selectedDirectory.toPath();
    }

    private void submitPin(String pin) {
        RSACipher rsaCipher = new RSACipher(pin);

        Path privateKeySavePath = Path.of("D:\\test");
        privateKeyPath.setText(privateKeySavePath.toString());

        Path publicKeySavePath = getPath();

        if (Objects.isNull(publicKeySavePath)) {
            return;
        }

        publicKeyPath.setText(publicKeySavePath.toString());

        try {
            rsaCipher.generateKeyPair();
            rsaCipher.saveKey(privateKeySavePath.toString(), KeyType.PRIVATE_KEY);
            rsaCipher.saveKey(publicKeySavePath.toString(), KeyType.PUBLIC_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
