package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.functions;

public class AckleyFunction implements Function3D {

	@Override
	public double getValue(double x, double y) {
		return -20 * Math.exp(-0.2 * Math.sqrt(0.5 * (x * x + y * y))) - Math.exp(0.5 * (Math.cos(2 * Math.PI * x) + Math.cos(2 * Math.PI * y))) + Math.E + 20;
	}

}
