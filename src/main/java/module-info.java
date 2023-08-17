module pl.edu.pw.gardockt.fireflyvisualizer {
	requires javafx.controls;
	requires javafx.fxml;
	requires com.opencsv;

	exports pl.edu.pw.gardockt.fireflyvisualizer;
	opens pl.edu.pw.gardockt.fireflyvisualizer to javafx.fxml;
	exports pl.edu.pw.gardockt.fireflyvisualizer.window;
	opens pl.edu.pw.gardockt.fireflyvisualizer.window to javafx.fxml;
	exports pl.edu.pw.gardockt.fireflyvisualizer.model;
	opens pl.edu.pw.gardockt.fireflyvisualizer.model to javafx.fxml;
	exports pl.edu.pw.gardockt.fireflyvisualizer.window.components to javafx.fxml;
	opens pl.edu.pw.gardockt.fireflyvisualizer.window.components;
    exports pl.edu.pw.gardockt.fireflyvisualizer.model.domain;
    opens pl.edu.pw.gardockt.fireflyvisualizer.model.domain to javafx.fxml;
	exports pl.edu.pw.gardockt.fireflyvisualizer.window.utilities;
	opens pl.edu.pw.gardockt.fireflyvisualizer.window.utilities to javafx.fxml;
}