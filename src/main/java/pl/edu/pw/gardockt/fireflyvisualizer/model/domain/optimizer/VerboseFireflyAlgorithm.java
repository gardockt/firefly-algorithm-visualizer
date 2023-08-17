package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer;

import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Function3DFragment;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Point3D;

import java.util.*;

public class VerboseFireflyAlgorithm {

    private final FireflyAlgorithmWithHistoryGetter algorithm = new FireflyAlgorithmWithHistoryGetter();

    public Map<Integer, Collection<Point3D>> optimize(Function3DFragment functionFragment,
                                                      FireflyAlgorithmConfiguration configuration) {
        algorithm.optimize(functionFragment, configuration);
        return algorithm.getLastRunSolutionsHistory();
    }

    public void setStartingSolutions(Collection<Point3D> solutions) {
        algorithm.setStartingSolutions(solutions);
    }
}
