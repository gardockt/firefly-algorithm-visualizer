package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer;

import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Function3DFragment;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Point3D;

import java.util.*;

public class FireflyAlgorithmWithHistoryGetter extends FireflyAlgorithm {

	private final Map<Integer, Collection<Point3D>> solutionsHistory = new HashMap<>();

	private void clean() {
		solutionsHistory.clear();
	}

	@Override
	public Point3D optimize(Function3DFragment functionFragment,
							FireflyAlgorithmConfiguration configuration,
							OptimizationCondition comparison) {
		clean();
		return super.optimize(functionFragment, configuration, comparison);
	}

	private Collection<Point3D> deepClonePoint3DCollection(Collection<Point3D> collection) {
		Collection<Point3D> copy = new ArrayList<>(collection.size());
		for (Point3D point : collection) {
			copy.add(point.clone());
		}
		return copy;
	}

	@Override
	protected Collection<Point3D> nextGeneration(Collection<Point3D> solutions,
												 int currentGeneration,
												 Function3DFragment functionFragment,
												 FireflyAlgorithmConfiguration configuration,
												 OptimizationCondition comparison) {
		if (currentGeneration == 0) {
			solutionsHistory.put(0, deepClonePoint3DCollection(solutions));
		}

		Collection<Point3D> newSolutions = super.nextGeneration(solutions, currentGeneration, functionFragment, configuration, comparison);
		solutionsHistory.put(currentGeneration + 1, deepClonePoint3DCollection(newSolutions));
		return newSolutions;
	}

	public Map<Integer, Collection<Point3D>> getLastRunSolutionsHistory() {
		return solutionsHistory;
	}
}
