package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer;

import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.OptimizationMode;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.optimizer.randomness.RandomnessFunction;

public class FireflyAlgorithmConfiguration {

	private OptimizationMode optimizationMode;
	private double lightAbsorptionCoefficient; // gamma
	private double fullAttractiveness; // beta_zero (attractiveness at r = 0)
	private RandomnessFunction randomnessFunction; // alpha
	private int maxGenerationCount;
	private int maxGenerationCountStagnation;
	private int solutionCount;

	public OptimizationMode getOptimizationMode() {
		return optimizationMode;
	}

	public void setOptimizationMode(OptimizationMode optimizationMode) {
		this.optimizationMode = optimizationMode;
	}

	public double getLightAbsorptionCoefficient() {
		return lightAbsorptionCoefficient;
	}

	public void setLightAbsorptionCoefficient(double lightAbsorptionCoefficient) {
		this.lightAbsorptionCoefficient = lightAbsorptionCoefficient;
	}

	public RandomnessFunction getRandomnessFunction() {
		return randomnessFunction;
	}

	public void setRandomnessFunction(RandomnessFunction randomnessFunction) {
		this.randomnessFunction = randomnessFunction;
	}

	public double getFullAttractiveness() {
		return fullAttractiveness;
	}

	public void setFullAttractiveness(double fullAttractiveness) {
		this.fullAttractiveness = fullAttractiveness;
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
}
