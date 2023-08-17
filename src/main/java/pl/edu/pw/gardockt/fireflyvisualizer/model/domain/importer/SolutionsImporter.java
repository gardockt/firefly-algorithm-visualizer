package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.importer;

import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Point2D;

import java.io.IOException;
import java.util.Collection;

public interface SolutionsImporter {

	Collection<Point2D> importFromFile(String path) throws IOException;

}
