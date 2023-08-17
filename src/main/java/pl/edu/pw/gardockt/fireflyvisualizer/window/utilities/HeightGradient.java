package pl.edu.pw.gardockt.fireflyvisualizer.window.utilities;

import javafx.scene.paint.Color;

import static pl.edu.pw.gardockt.fireflyvisualizer.Utilities.lerp;

public class HeightGradient {

	public static Color getColor(double pos) {
		if (pos < 0 || pos > 1) {
			throw new IllegalArgumentException("Argument musi zawierać się w przedziale [0, 1]");
		}

		Color[] colors = {
			new Color(0.25, 0.25, 1, 1), // blue
			new Color(0.25, 1, 0.25, 1), // green
			new Color(1, 1, 0.25, 1), // yellow
			new Color(1, 0.25, 0.25, 1)  // red
		};
		double[] points = {0.00, 0.50, 0.75, 1.00};

		int maxColorIndex = 0;
		do {
			maxColorIndex++;
		} while (pos > points[maxColorIndex]);

		Color minColor = colors[maxColorIndex - 1];
		Color maxColor = colors[maxColorIndex];
		double minPoint = points[maxColorIndex - 1];
		double maxPoint = points[maxColorIndex];

		double r = lerp(minPoint, maxPoint, pos, minColor.getRed(), maxColor.getRed());
		double g = lerp(minPoint, maxPoint, pos, minColor.getGreen(), maxColor.getGreen());
		double b = lerp(minPoint, maxPoint, pos, minColor.getBlue(), maxColor.getBlue());

		return new Color(r, g, b, 1);
	}

}
