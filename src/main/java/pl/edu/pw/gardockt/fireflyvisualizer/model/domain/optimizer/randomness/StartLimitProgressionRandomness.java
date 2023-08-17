package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer.randomness;

import java.util.ArrayList;
import java.util.List;

public class StartLimitProgressionRandomness implements RandomnessFunction {

	private final double startValue;
	private final double limitValue;

	public StartLimitProgressionRandomness(double startValue, double limitValue) {
		this.startValue = startValue;
		this.limitValue = limitValue;
	}

	@Override
	public double getValue(int t) {
		return limitValue + (startValue - limitValue) * Math.pow(Math.E, -t);
	}

	@Override
	public List<String> getParameterWarnings() {
		List<String> warnings = new ArrayList<>();

		if (startValue < 0) {
			warnings.add("Wartość początkowa współczynnika losowości powinna być nieujemna");
		}
		if (limitValue < 0) {
			warnings.add("Wartość graniczna współczynnika losowości powinna być nieujemna");
		}
		if (startValue < limitValue) {
			warnings.add("Wartość graniczna współczynnika losowości nie powinna być większa niż wartość początkowa");
		}

		return warnings;
	}
}
