package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.exporter;

import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Point3D;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CSVSolutionsExporter implements SolutionsExporter {

	@Override
	public void exportToFile(Map<Integer, Collection<Point3D>> solutions, String path) throws IOException {
		if (solutions == null) {
			throw new IllegalArgumentException("Historia optymalizacji nie może być null");
		}
		if (path == null) {
			throw new IllegalArgumentException("Ścieżka do pliku nie może być null");
		}

		List<String> lines = new ArrayList<>();
		lines.add("generation,x,y,z");
		for (var entry: solutions.entrySet()) {
			Integer generation = entry.getKey();
			for (Point3D solution: entry.getValue()) {
				lines.add(String.format(Locale.ENGLISH, "%d,%f,%f,%f", generation, solution.getX(), solution.getY(), solution.getZ()));
			}
		}

		Files.write(Paths.get(path), lines, StandardCharsets.UTF_8);
	}
}
