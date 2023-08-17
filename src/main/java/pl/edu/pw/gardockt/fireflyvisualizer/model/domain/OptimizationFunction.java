package pl.edu.pw.gardockt.fireflyvisualizer.model.domain;

import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.functions.*;

public enum OptimizationFunction {

	RASTRIGIN(RastriginFunction.class, "Funkcja Rastrigina"),
	ACKLEY(AckleyFunction.class, "Funkcja Ackleya"),
	HIMMELBLAU(HimmelblauFunction.class, "Funkcja Himmelblau"),
	ROSENBROCK(RosenbrockFunction.class, "Funkcja Rosenbrocka");

	private final Class<? extends Function3D> functionClass;
	private final String name;

	OptimizationFunction(Class<? extends Function3D> functionClass, String name) {
		this.functionClass = functionClass;
		this.name = name;
	}

	public Class<? extends Function3D> getFunctionClass() {
		return functionClass;
	}

	@Override
	public String toString() {
		return name;
	}

}
