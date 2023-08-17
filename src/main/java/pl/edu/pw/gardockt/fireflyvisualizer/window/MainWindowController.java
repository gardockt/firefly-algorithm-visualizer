package pl.edu.pw.gardockt.fireflyvisualizer.window;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.edu.pw.gardockt.fireflyvisualizer.exceptions.InvalidValueException;
import pl.edu.pw.gardockt.fireflyvisualizer.model.*;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Function3DFragment;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.OptimizationMode;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Point3D;
import pl.edu.pw.gardockt.fireflyvisualizer.window.components.*;

import java.io.File;
import java.net.URL;
import java.util.*;

import static pl.edu.pw.gardockt.fireflyvisualizer.Utilities.lerp;

public class MainWindowController implements Initializable, OptimizerObserver {

	// ### FXML CONTROLS ###

	// function display
	@FXML private FunctionCanvas functionCanvas;
	@FXML private RangeDisplay functionRangeDisplay;
	@FXML private TableView<Point3D> solutionsTable;
	@FXML private TableColumn<Point3D, Double> xColumn, yColumn, zColumn;
	@FXML private Button importSolutionsButton;
	@FXML private Button clearButton;
	@FXML private Button undoSolutionButton;
	@FXML private CheckBox logarithmicScaleCheckBox;

	// function generation
	@FXML private FunctionGenerationForm functionGenerationForm;

	// optimization
	@FXML private VBox algorithmParametersLayout;
	@FXML private AlgorithmConfigurationForm algorithmConfigurationForm;

	// preview control
	@FXML private SliderWithCounter displayedGenerationSlider;
	@FXML private VBox displayedGenerationManagementLayout;


	// ### STATE VARIABLES ###
	private final BooleanProperty functionGenerated = new SimpleBooleanProperty(false);
	private final BooleanProperty solutionsGenerated = new SimpleBooleanProperty(false);
	private final IntegerProperty displayedGeneration = new SimpleIntegerProperty(0);
	private final IntegerProperty lastGeneration = new SimpleIntegerProperty(0);

	private final IOptimizer optimizer = new Optimizer();


	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		optimizer.subscribe(this);

		functionRangeDisplay.minValueProperty().bind(functionCanvas.zMinProperty());
		functionRangeDisplay.maxValueProperty().bind(functionCanvas.zMaxProperty());

		logarithmicScaleCheckBox.disableProperty().bind(functionCanvas.logarithmicScalePossibleBinding().not());
		logarithmicScaleCheckBox.selectedProperty().bindBidirectional(functionCanvas.logarithmicScaleProperty());

		displayedGenerationSlider.setMin(0);
		displayedGenerationSlider.valueProperty().bindBidirectional(displayedGeneration);
		displayedGenerationSlider.maxProperty().bind(lastGeneration);

		displayedGeneration.addListener(e -> onDisplayedGenerationChanged());

		algorithmConfigurationForm.populationTextProperty().addListener(e -> onPopulationFieldValueChanged());

		displayedGenerationManagementLayout.disableProperty().bind(solutionsGenerated.not());

		importSolutionsButton.disableProperty().bind(functionGenerated.not().or(solutionsGenerated));
		algorithmParametersLayout.disableProperty().bind(functionGenerated.not());
		clearButton.disableProperty().bind(functionGenerated.not());

		algorithmConfigurationForm.setOptimizer(optimizer);

		xColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
		yColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
		zColumn.setCellValueFactory(new PropertyValueFactory<>("z"));

		// display exactly 5 decimal digits
		List<TableColumn<Point3D, Double>> columns = List.of(xColumn, yColumn, zColumn);
		for (TableColumn<Point3D, Double> column: columns) {
			column.setCellFactory(c -> new TableCell<>() {
				@Override
				protected void updateItem(Double value, boolean empty) {
					super.updateItem(value, empty);
					if (empty) {
						setText(null);
					} else {
						// Locale.ENGLISH for using dot as a decimal separator
						setText(String.format(Locale.ENGLISH, "%.5f", value));
					}
				}
			});
		}

		// disable placeholder
		solutionsTable.setPlaceholder(new Label());
	}


	// ### FXML ACTIONS ###

	@FXML
	private void onGenerateButtonPressed() {
		try {
			Function3DFragment functionFragment = functionGenerationForm.buildFunctionFragment();
			optimizer.setOptimizedFragment(functionFragment);
		} catch (Exception e) {
			displayError(e.getMessage());
		}
	}

	@FXML
	private void onFunctionCanvasMousePressed(MouseEvent e) {
		// if optimization is shown, do nothing
		if (solutionsGenerated.get()) {
			return;
		}

		Function3DFragment fragment = optimizer.getOptimizedFragment();

		// if no function is generated, do nothing
		if (fragment == null) {
			return;
		}

		double newSolutionX = lerp(0, functionCanvas.getWidth(), e.getX(), fragment.getXMin(), fragment.getXMax());
		double newSolutionY = lerp(functionCanvas.getHeight(), 0, e.getY(), fragment.getYMin(), fragment.getYMax()); // Y axis is inverted
		double newSolutionZ = fragment.getFunction().getValue(newSolutionX, newSolutionY);
		Point3D newSolution = new Point3D(newSolutionX, newSolutionY, newSolutionZ);

		optimizer.addStartingSolution(newSolution);
	}

	@FXML
	private void onOptimizeButtonPressed() {
		try {
			algorithmConfigurationForm.updateOptimizerConfiguration();
			optimizer.optimize();

			lastGeneration.set(optimizer.getOptimizationResults().size() - 1);

			if (displayedGeneration.get() != lastGeneration.get()) {
				displayedGeneration.set(lastGeneration.get());
			} else {
				// fire displayedGeneration's listener - setter from previous block would not do that
				onDisplayedGenerationChanged();
			}

			// sort by Z value - ascending for minimum optimization, descending for maximum
			solutionsTable.getSortOrder().setAll(List.of(zColumn));
            zColumn.setSortType(optimizer.getOptimizationMode() == OptimizationMode.MIN ? TableColumn.SortType.ASCENDING : TableColumn.SortType.DESCENDING);

			solutionsGenerated.set(true);

			optimizer.getStartingSolutions().clear();

			List<String> warnings = optimizer.getParameterWarnings();
			if (warnings != null && !warnings.isEmpty()) {
				StringBuilder message = new StringBuilder("Wyniki optymalizacji mogą być nieoczekiwane:");
				for (String warning : warnings) {
					message.append("\n").append(warning);
				}
				displayMessage("Ostrzeżenie", message.toString(), Alert.AlertType.WARNING);
			}
		} catch (InvalidValueException e) {
			displayError(e.getMessage());
		} catch (Exception e) {
			displayError(e.getMessage());
			e.printStackTrace();
		}
	}

	@FXML
	private void onExportButtonPressed(Event event) {
		Node sourceNode = (Node) event.getSource();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Eksportuj rozwiązania");
		File file = fileChooser.showSaveDialog(sourceNode.getScene().getWindow());

		if (file != null) {
			try {
				String path = file.getAbsolutePath();
				optimizer.exportResultsToFile(path);
				displayMessage("Sukces", "Dane zapisane do pliku \"" + path + "\"", Alert.AlertType.INFORMATION);
			} catch (Exception e) {
				displayError("Błąd zapisywania: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void onClearButtonPressed() {
		clearOptimizationResults();
	}

	@FXML
	private void onPopulationFieldValueChanged() {
		try {
			List<Point3D> solutions = optimizer.getStartingSolutions();
			int value = Integer.parseInt(algorithmConfigurationForm.getPopulationText());
			if (value < 0) {
				throw new InvalidValueException();
			}

			if (value < solutions.size()) {
				// difference is likely to be an order of magnitude (by removing a character from the text field), so
				// recreating a list is the most efficient solution
				List<Point3D> newCreatedSolutions = new ArrayList<>(solutions.subList(solutions.size() - value, solutions.size()));
				optimizer.setStartingSolutions(newCreatedSolutions);
			}

			optimizer.setSolutionCount(value);
		} catch (NumberFormatException | InvalidValueException ignored) {}
	}

	@FXML
	private void onUndoSolutionButtonPressed() {
		List<Point3D> solutions = optimizer.getStartingSolutions();
		solutions.remove(solutions.size() - 1);
	}

	@FXML
	private void onImportSolutionsButtonPressed(Event event) {
		Node sourceNode = (Node) event.getSource();
		FileChooser fileChooser = new FileChooser();

		fileChooser.setTitle("Importuj osobników");
		File file = fileChooser.showOpenDialog(sourceNode.getScene().getWindow());

		if (file != null) {
			try {
				optimizer.importStartingSolutionsFromFile(file.getAbsolutePath());
				algorithmConfigurationForm.setPopulationText(Integer.toString(optimizer.getSolutionCount()));
			} catch (Exception e) {
				displayError("Błąd wczytywania: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void onStatsButtonPressed() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("graph-view.fxml"));
			Parent parent = fxmlLoader.load();
			GraphWindowController controller = fxmlLoader.getController();
			controller.setOptimizationData(optimizer.getOptimizationResults());

			Stage stage = new Stage();
			Scene scene = new Scene(parent);
			stage.setTitle("Firefly Visualizer - Statystyki");
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// ### LISTENER CALLBACKS ###

	private void onDisplayedGenerationChanged() {
		Collection<Point3D> solutions = optimizer.getOptimizationResultByGeneration(displayedGeneration.get());

		if (solutions == null) {
			// draw nothing
			solutions = List.of();
		}

		functionCanvas.drawSolutions(solutions);

		// update table with preserving sort order
        // LinkedHashMap's iteration order is element insertion order
        Map<TableColumn<Point3D, ?>, TableColumn.SortType> sortData = new LinkedHashMap<>();
        for (TableColumn<Point3D, ?> column : solutionsTable.getSortOrder()) {
            sortData.put(column, column.getSortType());
        }

		ObservableList<Point3D> observableList = FXCollections.observableArrayList(solutions);
		solutionsTable.setItems(observableList);

        solutionsTable.getSortOrder().setAll(sortData.keySet());
        for (var entry : sortData.entrySet()) {
            entry.getKey().setSortType(entry.getValue());
        }
	}

	@Override
	public void onStartingSolutionsChanged(List<Point3D> newValue) {
		if (!solutionsGenerated.get()) {
			// created solutions are displayed
			functionCanvas.drawSolutions(newValue, Color.WHITE);
			solutionsTable.getItems().setAll(newValue);
		}
		undoSolutionButton.setDisable(newValue.isEmpty());
	}

	@Override
	public void onOptimizedFragmentChanged(Function3DFragment newValue) {
		clearOptimizationResults();
		functionCanvas.drawFunction(newValue);
		functionGenerated.set(newValue != null);
	}


	// ### HELPER FUNCTIONS ###

	private void displayMessage(String title, String message, Alert.AlertType type) {
		Alert alert = new Alert(type, message);
		alert.setHeaderText(title);
		alert.setTitle(title);
		alert.show();
	}

	private void displayError(String message) {
		displayMessage("Błąd", message, Alert.AlertType.ERROR);
	}

	private void clearSolutions() {
		if (solutionsGenerated.get()) {
			solutionsGenerated.set(false);
			optimizer.getOptimizationResults().clear();

			// force the listener to fire
			if (displayedGeneration.get() != 0) {
				displayedGeneration.set(0);
			} else {
				onDisplayedGenerationChanged();
			}

			// starting solutions are already empty
		} else {
			optimizer.getStartingSolutions().clear();
		}
	}

	private void clearOptimizationResults() {
		clearSolutions();

		// clear sorting, so that created solutions will be displayed in their creating order
		solutionsTable.getSortOrder().clear();
	}

}