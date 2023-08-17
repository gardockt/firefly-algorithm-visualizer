package pl.edu.pw.gardockt.fireflyvisualizer.window.components;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import pl.edu.pw.gardockt.fireflyvisualizer.window.utilities.HeightGradient;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.ResourceBundle;

public class RangeDisplay extends VBox implements Initializable {

    @FXML private Canvas gradientCanvas;
    @FXML private Label minValueLabel, maxValueLabel;

    private final DoubleProperty minValue = new SimpleDoubleProperty(0);
    private final DoubleProperty maxValue = new SimpleDoubleProperty(0);

    public RangeDisplay() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("range-display.fxml"));
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
        // draw graph gradient
        int gradientWidth  = (int) gradientCanvas.getWidth();
        int gradientHeight = (int) gradientCanvas.getHeight();

        PixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteRgbInstance();
        byte[] pixelArray = new byte[gradientWidth * gradientHeight * 3];
        WritableImage gradient = new WritableImage(gradientWidth, gradientHeight);
        PixelWriter gradientPixelWriter = gradient.getPixelWriter();

        for (int canvasX = 0; canvasX < gradientWidth; canvasX++) {
            Color color = HeightGradient.getColor((double) canvasX / (gradientWidth - 1));
            pixelArray[canvasX * 3    ] = (byte)(color.getRed() * 255);
            pixelArray[canvasX * 3 + 1] = (byte)(color.getGreen() * 255);
            pixelArray[canvasX * 3 + 2] = (byte)(color.getBlue() * 255);
        }
        for (int canvasY = 1; canvasY < gradientHeight; canvasY++) {
            System.arraycopy(pixelArray, 0, pixelArray, canvasY * gradientWidth * 3, gradientWidth * 3);
        }
        gradientPixelWriter.setPixels(0, 0, gradientWidth, gradientHeight, pixelFormat, pixelArray, 0, gradientWidth * 3);
        gradientCanvas.getGraphicsContext2D().drawImage(gradient, 0, 0);

        String valueFormat = "%.5g";
        minValueLabel.textProperty().bind(minValue.asString(Locale.ENGLISH, valueFormat));
        maxValueLabel.textProperty().bind(maxValue.asString(Locale.ENGLISH, valueFormat));
    }

    public double getMinValue() {
        return minValue.get();
    }

    public DoubleProperty minValueProperty() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue.set(minValue);
    }

    public double getMaxValue() {
        return maxValue.get();
    }

    public DoubleProperty maxValueProperty() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue.set(maxValue);
    }
}
