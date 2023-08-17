package pl.edu.pw.gardockt.fireflyvisualizer.model;

import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Function3DFragment;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Point3D;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface OptimizerObserver {
    default void onOptimizationResultsChanged(Map<Integer, Collection<Point3D>> newValue) {}
    default void onOptimizedFragmentChanged(Function3DFragment newValue) {}
    default void onStartingSolutionsChanged(List<Point3D> newValue) {}
}
