package pl.edu.pw.gardockt.fireflyvisualizer.window.components;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Function3DFragment;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Point3D;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.functions.Function3D;
import pl.edu.pw.gardockt.fireflyvisualizer.window.utilities.HeightGradient;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import static pl.edu.pw.gardockt.fireflyvisualizer.Utilities.fitToRange;
import static pl.edu.pw.gardockt.fireflyvisualizer.Utilities.lerp;

public class FunctionCanvas extends StackPane implements Initializable {

    @FXML private Canvas functionGraphCanvas;
    @FXML private Canvas optimizationCanvas;

    private final BooleanProperty logarithmicScale = new SimpleBooleanProperty(false);

    private final DoubleProperty zMin = new SimpleDoubleProperty();
    private final DoubleProperty zEps = new SimpleDoubleProperty();
    private final DoubleProperty zMax = new SimpleDoubleProperty();
    private final ObjectProperty<Function3DFragment> drawnFunctionFragment = new SimpleObjectProperty<>();

    public FunctionCanvas() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("function-canvas.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateFunctionZRange() {
        int width  = (int) Math.ceil(functionGraphCanvas.getWidth());
        int height = (int) Math.ceil(functionGraphCanvas.getHeight());

        double min = Double.POSITIVE_INFINITY;
        double eps = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        double[] functionArgsX = new double[width];
        double[] functionArgsY = new double[height];

        Function3DFragment fragment = drawnFunctionFragment.get();

        for (int canvasX = 0; canvasX < width; canvasX++) {
            functionArgsX[canvasX] = lerp(0, width, canvasX, fragment.getXMin(), fragment.getXMax());
        }
        for (int canvasY = 0; canvasY < height; canvasY++) {
            functionArgsY[canvasY] = lerp(0, height, canvasY, fragment.getYMin(), fragment.getYMax());
        }

        Function3D function = fragment.getFunction();
        if (function == null) {
            throw new IllegalArgumentException("Rysowana funkcja nie może być null");
        }

        for (int canvasX = 0; canvasX < width; canvasX++) {
            double functionX = functionArgsX[canvasX];
            for (int canvasY = 0; canvasY < height; canvasY++) {
                double functionY = functionArgsY[canvasY];
                double value = function.getValue(functionX, functionY);
                if (value < min) {
                    min = value;
                }
                if (value < eps && value > 0) {
                    eps = value;
                }
                if (value > max) {
                    max = value;
                }
            }
        }

        zMin.set(min);
        zEps.set(eps);
        zMax.set(max);
    }

    private void redrawFunction() {
        int width  = (int) Math.ceil(functionGraphCanvas.getWidth());
        int height = (int) Math.ceil(functionGraphCanvas.getHeight());

        double min = zMin.get();
        double max = zMax.get();

        // strategy for drawing a function containing 0 is to replace that value with eps (function's
        // smallest positive value) divided by 10 (chosen arbitrarily)

        if (logarithmicScale.get() && min == 0) {
            min = zEps.get() / 10;
        }

        double[] functionArgsX = new double[width];
        double[] functionArgsY = new double[height];

        Function3DFragment fragment = drawnFunctionFragment.get();

        for (int canvasX = 0; canvasX < width; canvasX++) {
            functionArgsX[canvasX] = lerp(0, width, canvasX, fragment.getXMin(), fragment.getXMax());
        }
        for (int canvasY = 0; canvasY < height; canvasY++) {
            // Y order needs to be reversed, so that function's Y grows upwards
            functionArgsY[height - canvasY - 1] = lerp(0, height, canvasY, fragment.getYMin(), fragment.getYMax());
        }

        Function3D function = fragment.getFunction();
        WritableImage image = new WritableImage(width, height);
        PixelWriter imagePixelWriter = image.getPixelWriter();
        for (int canvasX = 0; canvasX < width; canvasX++) {
            double functionX = functionArgsX[canvasX];
            for (int canvasY = 0; canvasY < height; canvasY++) {
                double functionY = functionArgsY[canvasY];
                double value = function.getValue(functionX, functionY);
                double scaledValue;

                if (!logarithmicScale.get()) {
                    // linear scale
                    scaledValue = lerp(min, max, value, 0, 1);
                } else {
                    // logarithmic scale
                    scaledValue = Math.log(value / min) / Math.log(max / min);
                }

                Color color;
                if (!Double.isNaN(scaledValue)) {
                    scaledValue = fitToRange(0, 1, scaledValue); // in case of calculation's inaccuracy
                    color = HeightGradient.getColor(scaledValue);
                } else {
                    // rare, but unavoidable with very high values
                    color = Color.BLACK;
                }
                imagePixelWriter.setColor(canvasX, canvasY, color);
            }
        }
        functionGraphCanvas.getGraphicsContext2D().drawImage(image, 0, 0);
    }

    public void drawFunction(Function3DFragment fragment) {
        if (fragment == null) {
            throw new IllegalArgumentException("Rysowany fragment nie może być null");
        }

        drawnFunctionFragment.set(fragment);

        updateFunctionZRange();
        if (!isLogarithmicScalePossible()) {
            logarithmicScale.set(false);
        }

        redrawFunction();
    }

    private void drawSolution(Point3D solution, Color color) {
        if (solution == null) {
            throw new IllegalArgumentException("Rysowany osobnik nie może być null");
        }
        if (color == null) {
            throw new IllegalArgumentException("Kolor osobników nie może być null");
        }

        double radius = 5;
        GraphicsContext graphicsContext = optimizationCanvas.getGraphicsContext2D();
        Function3DFragment fragment = drawnFunctionFragment.get();

        if (fragment == null) {
            return;
        }

        double canvasX = lerp(fragment.getXMin(), fragment.getXMax(), solution.getX(), 0, optimizationCanvas.getWidth());
        double canvasY = lerp(fragment.getYMin(), fragment.getYMax(), solution.getY(), optimizationCanvas.getHeight(), 0); // Y axis is inverted

        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setFill(color);
        graphicsContext.fillOval(canvasX - radius, canvasY - radius, radius * 2, radius * 2);
        graphicsContext.strokeOval(canvasX - radius, canvasY - radius, radius * 2, radius * 2);
    }

    public void clearSolutions() {
        optimizationCanvas.getGraphicsContext2D().clearRect(0, 0, optimizationCanvas.getWidth(), optimizationCanvas.getHeight());
    }

    public void drawSolutions(Collection<Point3D> solutions, Color color) {
        if (solutions == null) {
            throw new IllegalArgumentException("Rysowane osobniki nie mogą być null");
        }
        if (color == null) {
            throw new IllegalArgumentException("Kolor osobników nie może być null");
        }
        clearSolutions();
        for (Point3D solution : solutions) {
            drawSolution(solution, color);
        }
    }

    public void drawSolutions(Collection<Point3D> solutions) {
        drawSolutions(solutions, Color.DEEPPINK);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // fill the graph with a black color
        GraphicsContext graphGraphicsContext = functionGraphCanvas.getGraphicsContext2D();
        graphGraphicsContext.setFill(Color.BLACK);
        graphGraphicsContext.fillRect(0, 0, functionGraphCanvas.getWidth(), functionGraphCanvas.getHeight());

        logarithmicScale.addListener(e -> onLogarithmicScaleValueChanged());
    }

    private void onLogarithmicScaleValueChanged() {
        // force it to be off if the function has negative values
        if (logarithmicScale.get() && !isLogarithmicScalePossible()) {
            Platform.runLater(() -> logarithmicScale.set(false));
            return;
        }

        drawFunction(drawnFunctionFragment.get());
    }

    public boolean isLogarithmicScalePossible() {
        return logarithmicScalePossibleBinding().get();
    }

    public BooleanBinding logarithmicScalePossibleBinding() {
        return drawnFunctionFragment.isNotNull().and(zMin.greaterThanOrEqualTo(0));
    }

    public double getZMin() {
        return zMin.get();
    }

    public ReadOnlyDoubleProperty zMinProperty() {
        return zMin;
    }

    public double getZMax() {
        return zMax.get();
    }

    public ReadOnlyDoubleProperty zMaxProperty() {
        return zMax;
    }

    // (set|get)Width needs to be here in order to make the FXML property "width" work
    public void setWidth(double width) {
        super.setWidth(width);
    }

    public void setHeight(double height) {
        super.setHeight(height);
    }

    public boolean isLogarithmicScale() {
        return logarithmicScale.get();
    }

    public BooleanProperty logarithmicScaleProperty() {
        return logarithmicScale;
    }

    public void setLogarithmicScale(boolean logarithmicScale) {
        this.logarithmicScale.set(logarithmicScale);
    }
}
