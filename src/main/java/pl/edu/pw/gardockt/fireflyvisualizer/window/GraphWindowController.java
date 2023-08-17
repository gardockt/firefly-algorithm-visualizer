package pl.edu.pw.gardockt.fireflyvisualizer.window;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Point3D;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GraphWindowController {

	@FXML private LineChart<Integer, Double> valuesByGenerationChart;
	@FXML private LineChart<Integer, Double> stdDevByGenerationChart;

	private Map<Integer, Collection<Point3D>> pointsByGeneration;

	public void setOptimizationData(Map<Integer, Collection<Point3D>> pointsByGeneration) {
		this.pointsByGeneration = pointsByGeneration;
		updateValues();
	}

	private Map<Integer, Double> reducePointsToDoubleByZValue(Map<Integer, Collection<Point3D>> map,
															  BinaryOperator<Double> reductionFunction) {
		return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
			entry -> entry.getValue().stream().map(Point3D::getZ).reduce(reductionFunction).orElseThrow()
		));
	}

	private Map<Integer, Double> mapPointsToDouble(Map<Integer, Collection<Point3D>> map,
												   Function<Point3D, Double> pointToDoubleFunction,
												   Function<Collection<Double>, Double> mapFunction) {
		return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
			entry -> mapFunction.apply(entry.getValue().stream().map(pointToDoubleFunction).collect(Collectors.toList()))
		));
	}

	private Map<Integer, Double> mapPointsToDoubleByZValue(Map<Integer, Collection<Point3D>> map,
														   Function<Collection<Double>, Double> mapFunction) {
		return mapPointsToDouble(map, Point3D::getZ, mapFunction);
	}


	private <X,Y> XYChart.Series<X,Y> mapToSeries(Map<X,Y> map) {
		XYChart.Series<X,Y> series = new XYChart.Series<>();
		series.getData().setAll(map.entrySet().stream().map(entry -> new XYChart.Data<>(entry.getKey(), entry.getValue())).collect(Collectors.toList()));
		return series;
	}

	private void updateValues() {
		updateValuesByGenerationGraph();
		updateStdDevByGenerationGraph();
	}

	private void updateValuesByGenerationGraph() {
		Map<Integer, Double> minValues = reducePointsToDoubleByZValue(pointsByGeneration, Double::min);
		Map<Integer, Double> avgValues = mapPointsToDoubleByZValue(pointsByGeneration, this::mean);
		Map<Integer, Double> maxValues = reducePointsToDoubleByZValue(pointsByGeneration, Double::max);

		XYChart.Series<Integer, Double> minSeries = mapToSeries(minValues);
		XYChart.Series<Integer, Double> avgSeries = mapToSeries(avgValues);
		XYChart.Series<Integer, Double> maxSeries = mapToSeries(maxValues);

		minSeries.setName("Najmniejsza wartość");
		avgSeries.setName("Średnia wartość");
		maxSeries.setName("Największa wartość");

		valuesByGenerationChart.setData(FXCollections.observableArrayList(List.of(minSeries, avgSeries, maxSeries)));
	}

	private double mean(Collection<Double> values) {
		double returnValue = 0.0;
		for (double value : values) {
			returnValue += value;
		}
		return returnValue / values.size();
	}

	private double standardDeviation(Collection<Double> values) {
		int size = values.size();
		double average = mean(values);
		double numerator = 0.0;

		for (double value : values) {
			numerator += Math.pow(value - average, 2);
		}

		return Math.sqrt(numerator / size);
	}

	private void updateStdDevByGenerationGraph() {
		Map<Integer, Double> stdDevsZ = mapPointsToDouble(pointsByGeneration, Point3D::getZ, this::standardDeviation);

		XYChart.Series<Integer, Double> stdDevZSeries = mapToSeries(stdDevsZ);

		stdDevZSeries.setName("Odchylenie standardowe wartości osobników");

		stdDevByGenerationChart.setData(FXCollections.observableArrayList(List.of(stdDevZSeries)));
	}
}
