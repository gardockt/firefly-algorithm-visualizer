package pl.edu.pw.gardockt.fireflyvisualizer.window.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import pl.edu.pw.gardockt.fireflyvisualizer.exceptions.InvalidValueException;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer.randomness.*;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class RandomnessParameterForm extends VBox implements Initializable {

    @FXML private ChoiceBox<RandomnessType> randomnessTypeChoiceBox;
    @FXML private GridPane constantRandomnessParametersLayout, geometricProgressionRandomnessParametersLayout,
                           startLimitProgressionRandomnessParametersLayout;
    @FXML private TextField constantRandomnessValueField;
    @FXML private TextField geometricProgressionRandomnessStartValueField, geometricProgressionRandomnessRatioField;
    @FXML private TextField startLimitProgressionRandomnessStartValueField, startLimitProgressionRandomnessLimitValueField;

    private final ObjectProperty<RandomnessType> selectedRandomnessType = new SimpleObjectProperty<>();
    private Map<RandomnessType, Pane> randomnessTypeLayouts;

    public RandomnessParameterForm() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("randomness-parameter-form.fxml"));
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
        randomnessTypeLayouts = Map.of(
                RandomnessType.CONSTANT, constantRandomnessParametersLayout,
                RandomnessType.GEOMETRIC_PROGRESSION, geometricProgressionRandomnessParametersLayout,
                RandomnessType.START_LIMIT_PROGRESSION, startLimitProgressionRandomnessParametersLayout
        );

        selectedRandomnessType.bind(randomnessTypeChoiceBox.valueProperty());
        selectedRandomnessType.addListener(e -> onRandomnessTypeChanged());

        randomnessTypeChoiceBox.getItems().addAll(RandomnessType.values());
        randomnessTypeChoiceBox.getSelectionModel().select(0);
    }

    private void onRandomnessTypeChanged() {
        for (Map.Entry<RandomnessType, Pane> entry : randomnessTypeLayouts.entrySet()) {
            entry.getValue().setVisible(selectedRandomnessType.get() == entry.getKey());
        }
    }

    public RandomnessFunction buildRandomnessFunction() throws InvalidValueException {
        RandomnessFunction randomnessFunction;

        switch (selectedRandomnessType.get()) {
            case CONSTANT:
                double constValue = Double.parseDouble(constantRandomnessValueField.getText());
                randomnessFunction = new ConstantRandomness(constValue);
                break;
            case GEOMETRIC_PROGRESSION:
                double geometricStartValue = Double.parseDouble(geometricProgressionRandomnessStartValueField.getText());
                double geometricRatio = Double.parseDouble(geometricProgressionRandomnessRatioField.getText());

                if (geometricRatio == 0) {
                    throw new InvalidValueException("Iloraz nie może być zerem");
                }

                randomnessFunction = new GeometricProgressionRandomness(geometricStartValue, geometricRatio);
                break;
            case START_LIMIT_PROGRESSION:
                double startLimitStart = Double.parseDouble(startLimitProgressionRandomnessStartValueField.getText());
                double startLimitLimit = Double.parseDouble(startLimitProgressionRandomnessLimitValueField.getText());
                randomnessFunction = new StartLimitProgressionRandomness(startLimitStart, startLimitLimit);
                break;
            default:
                throw new InvalidValueException("Nierozpoznany typ losowości");
        }

        return randomnessFunction;
    }

    public void setWidth(double width) {
        super.setWidth(width);
    }

    public void setHeight(double height) {
        super.setHeight(height);
    }

}