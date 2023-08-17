package pl.edu.pw.gardockt.fireflyvisualizer.model.domain;

import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.functions.Function3D;

public class Function3DFragment {

	private final Function3D function;
	private final double xMin, xMax, yMin, yMax;

	public Function3DFragment(Function3D function, double xMin, double xMax, double yMin, double yMax) {
		this.function = function;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}

	public Function3D getFunction() {
		return function;
	}

	public double getXMin() {
		return xMin;
	}

	public double getXMax() {
		return xMax;
	}

	public double getYMin() {
		return yMin;
	}

	public double getYMax() {
		return yMax;
	}

}
