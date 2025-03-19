package pl.bsk.project.bsk_project.component;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class PINHandler extends VBox {

    private final StringBuilder pin = new StringBuilder();

    private final Label pinLabel = new Label("");

    private final Consumer<String> submitHandler;

    private final Button submitButton = new Button("OK");

    public PINHandler(Consumer<String> submitHandler) {
        this.submitHandler = submitHandler;

        this.setStyle("-fx-font-size: 16px");
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10.0);

        Label headLabel = new Label("Wprowadź PIN:");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(5.0);
        gridPane.setVgap(5.0);

        for(int i=1; i<10; i++) {
            Button button = new Button();
            button.setText(String.valueOf(i));
            button.setOnAction(this::enterDigit);
            gridPane.add(button, (i-1) % 3, (i-1) / 3);
        }

        Button clearButton = new Button("C");
        clearButton.setOnAction(this::clearPin);

        Button zeroDigit = new Button("0");
        zeroDigit.setOnAction(this::enterDigit);

        submitButton.setOnAction(this::submitPin);
        submitButton.setDisable(true);

        gridPane.add(clearButton,0, 3);
        gridPane.add(zeroDigit,1, 3);
        gridPane.add(submitButton,2, 3);

        gridPane.getChildren()
                .forEach(element -> {
                    if (element instanceof Button) {
                        element.setStyle("-fx-pref-width: 45px; -fx-pref-height: 45px");
                    }
                });

        this.getChildren().addAll(headLabel, pinLabel, gridPane);
    }

    protected void enterDigit(ActionEvent event) {
        Button button = (Button) event.getSource();
        String digit = button.getText();

        if (pin.length() < 4) {
            pin.append(digit);
        }

        if (pin.length() == 4) {
            submitButton.setDisable(false);
        }

        pinLabel.setText(getPin());
    }

    protected void submitPin(ActionEvent event) {
        submitHandler.accept(getPin());
        submitButton.setDisable(true);
    }

    protected void clearPin(ActionEvent event) {
        pin.delete(0, 4);
        submitButton.setDisable(true);
        pinLabel.setText("");
        submitHandler.accept(getPin());
    }

    public String getPin() {
        return pin.toString();
    }
}
