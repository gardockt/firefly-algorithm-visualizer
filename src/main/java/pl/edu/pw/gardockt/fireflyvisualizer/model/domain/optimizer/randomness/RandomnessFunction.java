package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer.randomness;

import java.util.List;

public interface RandomnessFunction {

	double getValue(int t);

	default List<String> getParameterWarnings() {
		return List.of();
	}

}
