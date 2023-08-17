package pl.edu.pw.gardockt.fireflyvisualizer.model;

import javafx.beans.property.*;
import javafx.collections.*;
import pl.edu.pw.gardockt.fireflyvisualizer.exceptions.InvalidValueException;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Function3DFragment;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.OptimizationMode;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Point2D;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Point3D;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.exporter.CSVSolutionsExporter;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.exporter.SolutionsExporter;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.functions.Function3D;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.importer.CSVSolutionsImporter;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.importer.SolutionsImporter;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer.FireflyAlgorithmConfiguration;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer.VerboseFireflyAlgorithm;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer.randomness.RandomnessFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Optimizer implements IOptimizer {

    private final VerboseFireflyAlgorithm algorithm = new VerboseFireflyAlgorithm();

    private final ObservableMap<Integer, Collection<Point3D>> optimizationResults = FXCollections.observableHashMap();
    private final ObjectProperty<Function3DFragment> optimizedFragment = new SimpleObjectProperty<>();
    private final ObservableList<Point3D> startingSolutions = FXCollections.observableArrayList();

    List<OptimizerObserver> observers = new ArrayList<>();

    private OptimizationMode optimizationMode;
    private double lightAbsorptionCoefficient;
    private double fullAttractiveness;
    private RandomnessFunction randomnessFunction;
    private int maxGenerationCount;
    private int maxGenerationCountStagnation;
    private int solutionCount;


    public Optimizer() {
        optimizationResults.addListener((MapChangeListener<? super Integer, ? super Collection<Point3D>>) e -> {
            for (OptimizerObserver observer : observers) {
                try {
                    observer.onOptimizationResultsChanged(optimizationResults);
                } catch (Exception ignored) {}
            }
        });
        optimizedFragment.addListener(e -> {
            for (OptimizerObserver observer : observers) {
                try {
                    observer.onOptimizedFragmentChanged(optimizedFragment.get());
                } catch (Exception ignored) {}
            }
        });
        startingSolutions.addListener((ListChangeListener<? super Point3D>) e -> {
            for (OptimizerObserver observer : observers) {
                try {
                    observer.onStartingSolutionsChanged(startingSolutions);
                } catch (Exception ignored) {}
            }
        });
    }


    private FireflyAlgorithmConfiguration prepareConfiguration() {
        FireflyAlgorithmConfiguration configuration = new FireflyAlgorithmConfiguration();

        configuration.setOptimizationMode(optimizationMode);
        configuration.setLightAbsorptionCoefficient(lightAbsorptionCoefficient);
        configuration.setFullAttractiveness(fullAttractiveness);
        configuration.setRandomnessFunction(randomnessFunction);
        configuration.setMaxGenerationCount(maxGenerationCount);
        configuration.setMaxGenerationCountStagnation(maxGenerationCountStagnation);
        configuration.setSolutionCount(solutionCount);

        return configuration;
    }

    public void optimize() {
        FireflyAlgorithmConfiguration configuration = prepareConfiguration();
        algorithm.setStartingSolutions(new ArrayList<>(startingSolutions));

        Map<Integer, Collection<Point3D>> solutions = algorithm.optimize(optimizedFragment.get(), configuration);

        optimizationResults.clear();
        optimizationResults.putAll(solutions);
    }

    public void subscribe(OptimizerObserver observer) {
        observers.add(observer);
    }

    public boolean unsubscribe(OptimizerObserver observer) {
        return observers.remove(observer);
    }

    public void addStartingSolution(Point3D solution) {
        if (solution == null) {
            throw new IllegalArgumentException("Osobnik nie może być null");
        }

        // if no function is generated, do nothing
        if (optimizedFragment.get() == null) {
            return;
        }

        // create a copy to avoid firing a listener and fix ConcurrentModificationException
        List<Point3D> newCreatedSolutions = new ArrayList<>(startingSolutions);
        newCreatedSolutions.add(solution);

        if (newCreatedSolutions.size() > solutionCount) {
            // remove oldest solutions and redraw
            for (int i = newCreatedSolutions.size(); i > solutionCount; i--) {
                // this loop will probably be executed exactly once, so O(n) is not a problem
                newCreatedSolutions.remove(0);
            }
        }

        startingSolutions.setAll(newCreatedSolutions);
    }

    public void importStartingSolutionsFromFile(String path) throws IOException, InvalidValueException {
        if (path == null) {
            throw new IllegalArgumentException("Ścieżka do pliku nie może być null");
        }

        SolutionsImporter importer = new CSVSolutionsImporter();
        Collection<Point2D> pointsFromFile = importer.importFromFile(path);

        Function3DFragment fragment = optimizedFragment.get();
        if (fragment == null) {
            throw new InvalidValueException("Optymalizowana funkcja nie została wygenerowana");
        }

        double xMin = fragment.getXMin();
        double xMax = fragment.getXMax();
        double yMin = fragment.getYMin();
        double yMax = fragment.getYMax();
        Function3D function = fragment.getFunction();

        List<Point3D> convertedPoints = new ArrayList<>();
        for (Point2D point : pointsFromFile) {
            double x = point.getX();
            double y = point.getY();
            if (x < xMin || x > xMax || y < yMin || y > yMax) {
                throw new InvalidValueException("Punkt " + point + " znajduje się poza zakresem");
            }
            convertedPoints.add(new Point3D(x, y, function.getValue(x, y)));
        }

        solutionCount = convertedPoints.size();
        startingSolutions.setAll(convertedPoints);
    }

    public void exportResultsToFile(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Ścieżka do pliku nie może być null");
        }

        SolutionsExporter solutionsExporter = new CSVSolutionsExporter();
        solutionsExporter.exportToFile(optimizationResults, path);
    }

    public List<String> getParameterWarnings() {
        List<String> warnings = new ArrayList<>();

        if (lightAbsorptionCoefficient < 0) {
            warnings.add("Współczynnik pochłaniania światła powinien być dodatni");
        }
        if (fullAttractiveness < 0 || fullAttractiveness > 1) {
            warnings.add("Atrakcyjność przy zerowej odległości powinna zawierać się w przedziale [0, 1]");
        }

        List<String> randomnessParameterWarnings = randomnessFunction.getParameterWarnings();
        if (randomnessParameterWarnings != null) {
            warnings.addAll(randomnessParameterWarnings);
        }

        return warnings;
    }

    public Map<Integer, Collection<Point3D>> getOptimizationResults() {
        return optimizationResults;
    }

    public Collection<Point3D> getOptimizationResultByGeneration(int generation) {
        return optimizationResults.get(generation);
    }

    public Function3DFragment getOptimizedFragment() {
        return optimizedFragment.get();
    }

    public void setOptimizedFragment(Function3DFragment optimizedFragment) {
        this.optimizedFragment.set(optimizedFragment);
    }

    public double getLightAbsorptionCoefficient() {
        return lightAbsorptionCoefficient;
    }

    public void setLightAbsorptionCoefficient(double lightAbsorptionCoefficient) {
        this.lightAbsorptionCoefficient = lightAbsorptionCoefficient;
    }

    public double getFullAttractiveness() {
        return fullAttractiveness;
    }

    public void setFullAttractiveness(double fullAttractiveness) {
        this.fullAttractiveness = fullAttractiveness;
    }

    public RandomnessFunction getRandomnessFunction() {
        return randomnessFunction;
    }

    public void setRandomnessFunction(RandomnessFunction randomnessFunction) {
        this.randomnessFunction = randomnessFunction;
    }

    public int getMaxGenerationCount() {
        return maxGenerationCount;
    }

    public void setMaxGenerationCount(int maxGenerationCount) {
        this.maxGenerationCount = maxGenerationCount;
    }

    public int getMaxGenerationCountStagnation() {
        return maxGenerationCountStagnation;
    }

    public void setMaxGenerationCountStagnation(int maxGenerationCountStagnation) {
        this.maxGenerationCountStagnation = maxGenerationCountStagnation;
    }

    public int getSolutionCount() {
        return solutionCount;
    }

    public void setSolutionCount(int solutionCount) {
        this.solutionCount = solutionCount;
    }

    public List<Point3D> getStartingSolutions() {
        return startingSolutions;
    }

    public void setStartingSolutions(Collection<Point3D> solutions) {
        startingSolutions.setAll(solutions);
    }

    public OptimizationMode getOptimizationMode() {
        return optimizationMode;
    }

    public void setOptimizationMode(OptimizationMode optimizationMode) {
        this.optimizationMode = optimizationMode;
    }
}
