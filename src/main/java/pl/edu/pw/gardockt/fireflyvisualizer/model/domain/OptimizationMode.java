package pl.edu.pw.gardockt.fireflyvisualizer.model.domain;

public enum OptimizationMode {

	MIN("minimum"),
	MAX("maksimum");

	private final String displayName;

	OptimizationMode(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}

}
