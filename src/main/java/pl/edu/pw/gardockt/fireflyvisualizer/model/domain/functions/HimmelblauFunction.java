package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.functions;

public class HimmelblauFunction implements Function3D {

    @Override
    public double getValue(double x, double y) {
        return Math.pow(x * x + y - 11, 2) + Math.pow(x + y * y - 7, 2);
    }

}
