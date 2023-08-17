package pl.edu.pw.gardockt.fireflyvisualizer;

public class Utilities {

	public static double lerp(double varAMin, double varAMax, double varA, double varBMin, double varBMax) {
		return (varA - varAMin) / (varAMax - varAMin) * (varBMax - varBMin) + varBMin;
	}

	public static int fitToRange(int min, int max, int value) {
		if (min > max) {
			throw new IllegalArgumentException("Minimum nie może być większe od maksimum");
		}
		return Math.min(Math.max(value, min), max);
	}

	public static double fitToRange(double min, double max, double value) {
		if (min > max) {
			throw new IllegalArgumentException("Minimum nie może być większe od maksimum");
		}
		return Math.min(Math.max(value, min), max);
	}

}
