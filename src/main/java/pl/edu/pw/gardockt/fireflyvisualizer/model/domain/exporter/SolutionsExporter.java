package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.exporter;

import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Point3D;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public interface SolutionsExporter {

	void exportToFile(Map<Integer, Collection<Point3D>> solutions, String path) throws IOException;

}
