package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer.randomness;

import java.util.List;

public class ConstantRandomness implements RandomnessFunction {

	private final double value;

	public ConstantRandomness(double value) {
		this.value = value;
	}

	@Override
	public double getValue(int t) {
		return value;
	}

	@Override
	public List<String> getParameterWarnings() {
		if (value < 0) {
			return List.of("Wartość współczynnika losowości powinna być nieujemna");
		} else {
			return List.of();
		}
	}
}
