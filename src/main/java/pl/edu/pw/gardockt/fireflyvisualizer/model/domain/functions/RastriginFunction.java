package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.functions;

public class RastriginFunction implements Function3D {

	@Override
	public double getValue(double x, double y) {
		double A = 10;

		double value = 0;
		double[] argVec = {x, y};
		for (double arg : argVec) {
			value += arg * arg - A * Math.cos(2 * Math.PI * arg);
		}

		return A * argVec.length + value;
	}

}
