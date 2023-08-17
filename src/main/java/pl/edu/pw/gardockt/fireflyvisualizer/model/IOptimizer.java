package pl.edu.pw.gardockt.fireflyvisualizer.model;

import pl.edu.pw.gardockt.fireflyvisualizer.exceptions.InvalidValueException;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Function3DFragment;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.OptimizationMode;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Point3D;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer.randomness.RandomnessFunction;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IOptimizer {
    void optimize();

    void subscribe(OptimizerObserver observer);
    boolean unsubscribe(OptimizerObserver observer);

    void addStartingSolution(Point3D solution);

    void importStartingSolutionsFromFile(String path) throws IOException, InvalidValueException;
    void exportResultsToFile(String path) throws IOException;

    List<String> getParameterWarnings();

    Collection<Point3D> getOptimizationResultByGeneration(int generation);
    Map<Integer, Collection<Point3D>> getOptimizationResults();
    Function3DFragment getOptimizedFragment();
    void setOptimizedFragment(Function3DFragment optimizedFragment);
    double getLightAbsorptionCoefficient();
    void setLightAbsorptionCoefficient(double lightAbsorptionCoefficient);
    double getFullAttractiveness();
    void setFullAttractiveness(double fullAttractiveness);
    RandomnessFunction getRandomnessFunction();
    void setRandomnessFunction(RandomnessFunction randomnessFunction);
    int getMaxGenerationCount();
    void setMaxGenerationCount(int maxGenerationCount);
    int getMaxGenerationCountStagnation();
    void setMaxGenerationCountStagnation(int maxGenerationCountStagnation);
    int getSolutionCount();
    void setSolutionCount(int solutionCount);
    List<Point3D> getStartingSolutions();
    void setStartingSolutions(Collection<Point3D> solutions);
    OptimizationMode getOptimizationMode();
    void setOptimizationMode(OptimizationMode optimizationMode);
}
