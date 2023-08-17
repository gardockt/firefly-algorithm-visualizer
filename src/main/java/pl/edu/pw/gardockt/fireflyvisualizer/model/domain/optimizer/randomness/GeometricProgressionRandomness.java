package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer.randomness;

import java.util.ArrayList;
import java.util.List;

public class GeometricProgressionRandomness implements RandomnessFunction {

	private final double initialValue;
	private final double ratio;

	public GeometricProgressionRandomness(double initialValue, double ratio) {
		if (ratio == 0) {
			throw new IllegalArgumentException("Iloraz nie może być zerem");
		}

		this.initialValue = initialValue;
		this.ratio = ratio;
	}

	@Override
	public double getValue(int t) {
		return initialValue * Math.pow(ratio, t);
	}

	@Override
	public List<String> getParameterWarnings() {
		List<String> warnings = new ArrayList<>();

		if (initialValue < 0) {
			warnings.add("Wartość początkowa współczynnika losowości powinna być nieujemna");
		}

		if (ratio < 0) {
			warnings.add("Iloraz współczynnika losowości powinien być nieujemny");
		} else if (ratio > 1) {
			warnings.add("Iloraz współczynnika losowości nie powinien być większy niż 1");
		}

		return warnings;
	}
}
