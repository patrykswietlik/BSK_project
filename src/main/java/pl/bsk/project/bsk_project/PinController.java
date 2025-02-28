package pl.bsk.project.bsk_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Text;

import java.util.List;

public class PinController {

    @FXML
    private Label pinPlaceHolder;

    @FXML
    private Button submitButton;

    @FXML
    private Button privateKeyButton;

    @FXML
    private Button publicKeyButton;

    private List<String> keys;

    private final StringBuilder pin = new StringBuilder();

    @FXML
    protected void enterDigit(ActionEvent event) {
        Button button = (Button) event.getSource();
        String digit = button.getText();

        if (pin.length() < 4) {
            pin.append(digit);
        }

        refreshDisplayedPin();
    }

    @FXML
    protected void clearPin() {
        pin.delete(0, 4);
        refreshDisplayedPin();
    }

    @FXML
    protected void submitPin() throws Exception {
        keys = KeysGenerator.getRSAKeys(pin.toString());
        privateKeyButton.setDisable(false);
        publicKeyButton.setDisable(false);
    }

    @FXML
    protected void copyPrivateKey() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(keys.get(0));
        clipboard.setContent(content);
    }

    @FXML
    protected void copyPublicKey() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(keys.get(1));
        clipboard.setContent(content);
    }

    private void refreshDisplayedPin() {
        pinPlaceHolder.setText(
                pin.toString()
        );

        submitButton.setDisable(
                pin.length() != 4
        );
    }

    public String getPin() {
        return pin.toString();
    }

}
