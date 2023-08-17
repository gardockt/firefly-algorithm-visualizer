package pl.edu.pw.gardockt.fireflyvisualizer.window.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import pl.edu.pw.gardockt.fireflyvisualizer.exceptions.InvalidValueException;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Function3DFragment;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.OptimizationFunction;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.functions.Function3D;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FunctionGenerationForm extends GridPane implements Initializable {

    @FXML private ComboBox<OptimizationFunction> functionComboBox;
    @FXML private TextField xMinField, xMaxField, yMinField, yMaxField;

    public FunctionGenerationForm() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("function-generation-form.fxml"));
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
        functionComboBox.getItems().addAll(OptimizationFunction.values());
        functionComboBox.getSelectionModel().select(0);
    }

    public Function3DFragment buildFunctionFragment() throws InvalidValueException {
        OptimizationFunction selectedFunction = functionComboBox.getValue();
        Function3D optimizedFunction;

        if (selectedFunction == null) {
            throw new InvalidValueException("Nie wybrano funkcji");
        }

        try {
            optimizedFunction = functionComboBox.getValue().getFunctionClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        double xMin, xMax, yMin, yMax;

        try {
            xMin = Double.parseDouble(xMinField.getText());
            xMax = Double.parseDouble(xMaxField.getText());
            yMin = Double.parseDouble(yMinField.getText());
            yMax = Double.parseDouble(yMaxField.getText());
        } catch (NumberFormatException e) {
            throw new InvalidValueException("Wprowadzone wartości muszą być wartościami liczbowymi");
        }

        if (xMin >= xMax || yMin >= yMax) {
            throw new InvalidValueException("Niewłaściwy zakres");
        }

        return new Function3DFragment(optimizedFunction, xMin, xMax, yMin, yMax);
    }

}
