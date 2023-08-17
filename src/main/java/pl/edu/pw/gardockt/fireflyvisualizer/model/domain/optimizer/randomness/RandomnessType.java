package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer.randomness;

public enum RandomnessType {
	CONSTANT("stała"),
	GEOMETRIC_PROGRESSION("ciąg geometryczny"),
	START_LIMIT_PROGRESSION("ciąg o danej wartości początkowej i granicznej");

	private final String displayedName;

	RandomnessType(String displayedName) {
		this.displayedName = displayedName;
	}

	@Override
	public String toString() {
		return displayedName;
	}
}
