package pl.edu.pw.gardockt.fireflyvisualizer.window.components;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static pl.edu.pw.gardockt.fireflyvisualizer.Utilities.fitToRange;

public class SliderWithCounter extends HBox implements Initializable {

    @FXML private Slider slider;
    @FXML private Button incrementButton, decrementButton;
    @FXML private TextField valueField;

    // to force integer values
    private final IntegerProperty value = new SimpleIntegerProperty(0);
    private final IntegerProperty min = new SimpleIntegerProperty(0);
    private final IntegerProperty max = new SimpleIntegerProperty(100);

    public SliderWithCounter() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("slider-with-counter.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        slider.valueProperty().bindBidirectional(value);
        slider.minProperty().bindBidirectional(min);
        slider.maxProperty().bindBidirectional(max);

        // workaround that allows binding without setting TextField as read-only
        value.addListener(e -> Platform.runLater(this::onValueChanged));
        valueField.textProperty().addListener(e -> Platform.runLater(this::onValueFieldValueChanged));

        decrementButton.disableProperty().bind(value.lessThanOrEqualTo(min));
        incrementButton.disableProperty().bind(value.greaterThanOrEqualTo(max));
    }

    @FXML
    private void onDecrementButtonPressed() {
        value.set(fitToRange(minProperty().get(), maxProperty().get(), value.get() - 1));
    }

    @FXML
    private void onIncrementButtonPressed() {
        value.set(fitToRange(minProperty().get(), maxProperty().get(), value.get() + 1));
    }

    private void onValueFieldValueChanged() {
        try {
            int newValue = Integer.parseInt(valueField.getText());
            value.set(fitToRange(min.get(), max.get(), newValue));
        } catch (Exception ignored) {}
    }

    private void onValueChanged() {
        boolean shouldUpdate = true;
        try {
            int valueFieldValue = Integer.parseInt(valueField.getText());
            shouldUpdate = (valueFieldValue != value.get());
        } catch (NumberFormatException ignored) {}

        if (shouldUpdate) {
            // either the field contains an invalid value, or the value has been changed in other ways - change
            // the field's value and move caret to the end (by default the caret is moved to home, which feels
            // incorrect, and the field's value needs to be changed, so saving caret position is pointless)
            valueField.setText(Integer.toString(value.get()));
            valueField.end();
        }
    }

    public int getValue() {
        return value.get();
    }

    public IntegerProperty valueProperty() {
        return value;
    }

    public void setValue(int value) {
        this.value.set(value);
    }

    public int getMin() {
        return min.get();
    }

    public IntegerProperty minProperty() {
        return min;
    }

    public void setMin(int min) {
        this.min.set(min);
    }

    public int getMax() {
        return max.get();
    }

    public IntegerProperty maxProperty() {
        return max;
    }

    public void setMax(int max) {
        this.max.set(max);
    }
}
