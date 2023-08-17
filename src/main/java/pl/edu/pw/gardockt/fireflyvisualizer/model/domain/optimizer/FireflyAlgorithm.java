package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer;

import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Function3DFragment;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.OptimizationMode;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Point3D;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.functions.Function3D;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer.randomness.RandomnessFunction;

import java.util.*;
import java.util.stream.Collectors;

import static pl.edu.pw.gardockt.fireflyvisualizer.Utilities.fitToRange;

public class FireflyAlgorithm {

    private final Random random = new Random();

    private Collection<Point3D> fireflies;

    public void setStartingSolutions(Collection<Point3D> solutions) {
        this.fireflies = solutions;
    }

    public Point3D optimize(Function3DFragment functionFragment, FireflyAlgorithmConfiguration configuration) {
        OptimizationMode optimizationMode = configuration.getOptimizationMode();
        switch (optimizationMode) {
            case MIN:
                return optimize(functionFragment, configuration, (pa, pb) -> pa.getZ() < pb.getZ());
            case MAX:
                return optimize(functionFragment, configuration, (pa, pb) -> pa.getZ() > pb.getZ());
            default:
                throw new IllegalArgumentException("Nierozpoznany tryb optymalizacji");
        }
    }

    public Point3D optimize(Function3DFragment functionFragment,
                            FireflyAlgorithmConfiguration configuration,
                            OptimizationCondition comparison) {
        if (functionFragment == null) {
            throw new IllegalArgumentException("Optymalizowany fragment nie może być null");
        }
        if (configuration == null) {
            throw new IllegalArgumentException("Konfiguracja nie może być null");
        }
        if (comparison == null) {
            throw new IllegalArgumentException("Funkcja porównywania nie może być null");
        }

        int solutionCount = configuration.getSolutionCount();
        int generationCount = configuration.getMaxGenerationCount();

        if (solutionCount <= 0) {
            throw new IllegalArgumentException("Liczba osobników musi być dodatnia");
        }
        if (generationCount <= 0) {
            throw new IllegalArgumentException("Liczba pokoleń musi być dodatnia");
        }

        Function3D function = functionFragment.getFunction();
        double xMin = functionFragment.getXMin();
        double xMax = functionFragment.getXMax();
        double yMin = functionFragment.getYMin();
        double yMax = functionFragment.getYMax();

        if (function == null) {
            throw new IllegalArgumentException("Optymalizowana funkcja nie może być null");
        }
        if (xMin >= xMax || yMin >= yMax) {
            throw new IllegalArgumentException("Optymalizowany przedział jest nieprawidłowy");
        }

        if (fireflies == null) {
            fireflies = new ArrayList<>();
        } else {
            // limit solution count to given size, if starting solutions' size exceeds it
            if (fireflies.size() > solutionCount) {
                fireflies = fireflies.stream().limit(solutionCount).collect(Collectors.toList());
            }
        }

        // generate initial population
        for (int i = fireflies.size(); i < solutionCount; i++) {
            double x = random.nextDouble() * (xMax - xMin) + xMin;
            double y = random.nextDouble() * (yMax - yMin) + yMin;
            double z = function.getValue(x, y);
            fireflies.add(new Point3D(x, y, z));
        }

        Point3D bestFirefly = null;
        for (Point3D firefly : fireflies) {
            if (bestFirefly == null || comparison.isBetter(firefly, bestFirefly)) {
                bestFirefly = firefly.clone();
            }
        }

        int maxStagnationGenerations = configuration.getMaxGenerationCountStagnation();
        int stagnationGenerations = 0;

        if (maxStagnationGenerations < 0) {
            throw new IllegalArgumentException("Liczba pokoleń bez poprawy nie może być ujemna");
        }

        for (int gen = 0; gen < generationCount; gen++) {
            fireflies = nextGeneration(fireflies, gen, functionFragment, configuration, comparison);

            boolean improvementMade = false;
            for (Point3D firefly : fireflies) {
                if (comparison.isBetter(firefly, bestFirefly)) {
                    bestFirefly = firefly.clone();
                    improvementMade = true;
                }
            }

            if (!improvementMade) {
                if (++stagnationGenerations > maxStagnationGenerations) {
                    break;
                }
            } else {
                stagnationGenerations = 0;
            }
        }

        return bestFirefly;
    }

    protected Collection<Point3D> nextGeneration(Collection<Point3D> solutions,
                                                 int currentGeneration,
                                                 Function3DFragment functionFragment,
                                                 FireflyAlgorithmConfiguration configuration,
                                                 OptimizationCondition comparison) {
        if (solutions == null) {
            throw new IllegalArgumentException("Pokolenie nie może być null");
        }
        if (functionFragment == null) {
            throw new IllegalArgumentException("Optymalizowany fragment nie może być null");
        }
        if (configuration == null) {
            throw new IllegalArgumentException("Konfiguracja nie może być null");
        }
        if (comparison == null) {
            throw new IllegalArgumentException("Funkcja porównująca nie może być null");
        }

        double fullAttractiveness = configuration.getFullAttractiveness();
        double lightAbsorptionCoefficient = configuration.getLightAbsorptionCoefficient();
        RandomnessFunction randomnessFunction = configuration.getRandomnessFunction();

        if (randomnessFunction == null) {
            throw new IllegalArgumentException("Funkcja losowości nie może być null");
        }

        Function3D function = functionFragment.getFunction();
        double xMin = functionFragment.getXMin();
        double xMax = functionFragment.getXMax();
        double yMin = functionFragment.getYMin();
        double yMax = functionFragment.getYMax();

        if (function == null) {
            throw new IllegalArgumentException("Optymalizowana funkcja nie może być null");
        }
        if (xMin >= xMax || yMin >= yMax) {
            throw new IllegalArgumentException("Optymalizowany przedział jest nieprawidłowy");
        }

        List<Point3D> newSolutions = new ArrayList<>(solutions);

        for (int i = 0; i < newSolutions.size(); i++) {
            Point3D attractedFirefly = newSolutions.get(i);
            boolean betterFireflyFound = false;

            for (int j = 0; j < newSolutions.size(); j++) {
                Point3D attractingFirefly = newSolutions.get(j);

                if (comparison.isBetter(attractingFirefly, attractedFirefly)) {
                    betterFireflyFound = true;

                    // move
                    double distanceSquared = Math.pow(attractingFirefly.getX() - attractedFirefly.getX(), 2) +
                                             Math.pow(attractingFirefly.getY() - attractedFirefly.getY(), 2);
                    double beta = fullAttractiveness * Math.pow(Math.E, -lightAbsorptionCoefficient * distanceSquared);

                    double diffX = beta * (attractingFirefly.getX() - attractedFirefly.getX()) +
                                   randomnessFunction.getValue(currentGeneration) * (random.nextDouble() - 0.5);
                    double diffY = beta * (attractingFirefly.getY() - attractedFirefly.getY()) +
                                   randomnessFunction.getValue(currentGeneration) * (random.nextDouble() - 0.5);

                    attractedFirefly.setX(fitToRange(xMin, xMax, attractedFirefly.getX() + diffX));
                    attractedFirefly.setY(fitToRange(yMin, yMax, attractedFirefly.getY() + diffY));

                    attractedFirefly.setZ(function.getValue(attractedFirefly.getX(), attractedFirefly.getY()));
                }
            }

            if (!betterFireflyFound) {
                // move randomly
                double diffX = randomnessFunction.getValue(currentGeneration) * (random.nextDouble() - 0.5);
                double diffY = randomnessFunction.getValue(currentGeneration) * (random.nextDouble() - 0.5);

                attractedFirefly.setX(fitToRange(xMin, xMax, attractedFirefly.getX() + diffX));
                attractedFirefly.setY(fitToRange(yMin, yMax, attractedFirefly.getY() + diffY));

                attractedFirefly.setZ(function.getValue(attractedFirefly.getX(), attractedFirefly.getY()));
            }
        }

        return newSolutions;
    }

    @FunctionalInterface
    public interface OptimizationCondition {
        boolean isBetter(Point3D mainPoint, Point3D otherPoint);
    }
}
