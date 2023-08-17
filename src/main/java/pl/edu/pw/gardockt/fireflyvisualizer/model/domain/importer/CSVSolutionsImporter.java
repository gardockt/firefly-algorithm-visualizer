package pl.edu.pw.gardockt.fireflyvisualizer.model.domain.importer;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import pl.edu.pw.gardockt.fireflyvisualizer.model.domain.Point2D;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CSVSolutionsImporter implements SolutionsImporter {

	@Override
	public Collection<Point2D> importFromFile(String path) throws IOException {
		if (path == null) {
			throw new IllegalArgumentException("Ścieżka do pliku nie może być null");
		}

		try (CSVReader reader = new CSVReader(new FileReader(path))) {
			List<Point2D> solutions = new ArrayList<>();

			String[] line;
			for (int lineNumber = 1; (line = reader.readNext()) != null; lineNumber++) {
				// format: X,Y

				if (line.length != 2) {
					throw new IOException("Nieprawidłowa ilość wartości w linii " + lineNumber);
				}

				try {
					double x = Double.parseDouble(line[0]);
					double y = Double.parseDouble(line[1]);
					solutions.add(new Point2D(x, y));
				} catch (NumberFormatException e) {
					if (lineNumber == 1) {
						// might be a header
						System.err.println("Wykryto możliwy nagłówek, linia 1 pominięta");
					} else {
						throw new IOException("Linia " + lineNumber + " zawiera wartość niebędącą liczbą");
					}
				}
			}

			return solutions;
		} catch (CsvValidationException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

}
