package pl.edu.pw.gardockt.fireflyvisualizer.window.components;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pl.edu.pw.gardockt.fireflyvisualizer.exceptions.InvalidValueException;
import pl.edu.pw.gardockt.fireflyvisualizer.model.IOptimizer;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.OptimizationMode;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer.randomness.RandomnessFunction;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AlgorithmConfigurationForm extends VBox implements Initializable {

    @FXML private ChoiceBox<OptimizationMode> optimizationModeChoiceBox;
    @FXML private TextField lightAbsorptionCoefficientField;
    @FXML private TextField fullAttractivenessField;
    @FXML private TextField populationField;
    @FXML private TextField maxGenerationCountField;
    @FXML private TextField maxGenerationCountStagnationField;
    @FXML private VBox algorithmParametersLayout;
    @FXML private RandomnessParameterForm randomnessParameterForm;

    private IOptimizer optimizer;

    public AlgorithmConfigurationForm() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("algorithm-configuration-form.fxml"));
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
        optimizationModeChoiceBox.getItems().addAll(OptimizationMode.values());
        optimizationModeChoiceBox.getSelectionModel().select(0);
    }

    public void updateOptimizerConfiguration() throws InvalidValueException {
        if (optimizer == null) {
            throw new IllegalStateException("Optymalizator nie może być null");
        }

        try {
            double lightAbsorptionCoefficient = Double.parseDouble(lightAbsorptionCoefficientField.getText());
            double fullAttractiveness = Double.parseDouble(fullAttractivenessField.getText());
            int population = Integer.parseInt(populationField.getText());
            int maxGenerationCount = Integer.parseInt(maxGenerationCountField.getText());
            int maxGenerationCountStagnation;
            RandomnessFunction randomnessFunction = randomnessParameterForm.buildRandomnessFunction();

            String maxGenerationCountStagnationText = maxGenerationCountStagnationField.getText();
            if (!maxGenerationCountStagnationText.isBlank()) {
                maxGenerationCountStagnation = Integer.parseInt(maxGenerationCountStagnationText);
            } else {
                // force maxGenerationCount generations
                maxGenerationCountStagnation = maxGenerationCount;
            }

            if (population <= 0 || maxGenerationCount <= 0 || maxGenerationCountStagnation < 0) {
                throw new InvalidValueException("Wprowadzone wartości są spoza zakresu");
            }

            OptimizationMode optimizationMode = optimizationModeChoiceBox.getValue();

            optimizer.setOptimizationMode(optimizationMode);
            optimizer.setRandomnessFunction(randomnessFunction);
            optimizer.setLightAbsorptionCoefficient(lightAbsorptionCoefficient);
            optimizer.setFullAttractiveness(fullAttractiveness);
            optimizer.setSolutionCount(population);
            optimizer.setMaxGenerationCount(maxGenerationCount);
            optimizer.setMaxGenerationCountStagnation(maxGenerationCountStagnation);
            optimizer.setRandomnessFunction(randomnessFunction);
        } catch (NumberFormatException e) {
            throw new InvalidValueException("Wprowadzone wartości muszą być wartościami liczbowymi");
        }
    }

    public IOptimizer getOptimizer() {
        return optimizer;
    }

    public void setOptimizer(IOptimizer optimizer) {
        this.optimizer = optimizer;
    }

    public String getPopulationText() {
        return populationField.getText();
    }

    public StringProperty populationTextProperty() {
        return populationField.textProperty();
    }

    public void setPopulationText(String text) {
        populationField.setText(text);
    }

}
