package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.functions;

public class RosenbrockFunction implements Function3D {

    @Override
    public double getValue(double x, double y) {
        return 100 * Math.pow(y - x * x, 2) + Math.pow(1 - x, 2);
    }

}
