package software.ulpgc.kata5.application;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import software.ulpgc.kata5.architecture.io.Store;
import software.ulpgc.kata5.architecture.model.Movie;
import software.ulpgc.kata5.architecture.viewmodel.Histogram;
import software.ulpgc.kata5.architecture.viewmodel.HistogramBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Stream;

public class Desktop extends JFrame {
    private final Store store;

    private Desktop(Store store) throws HeadlessException {
        this.store = store;
        this.setTitle("Histogram");
        this.setResizable(false);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
    }

    public static Desktop create(Store store) throws HeadlessException {
        return new Desktop(store);
    }

    public Desktop display() {
        this.getContentPane().add(chartPnaleWith(histogram()));
        return this;
    }

    private ChartPanel chartPnaleWith(Histogram histogram) {
        return new ChartPanel(chartWith(histogram));
    }

    private JFreeChart chartWith(Histogram histogram) {
        return ChartFactory.createHistogram(
                histogram.title(),
                histogram.x(),
                histogram.y(),
                datasetWith(histogram)
        );
    }

    private XYSeriesCollection datasetWith(Histogram histogram) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesIn(histogram));
        return dataset;
    }

    private XYSeries seriesIn(Histogram histogram) {
        XYSeries xySeries = new XYSeries(histogram.legend());
        for (int bin : histogram) {
            xySeries.add(bin, histogram.count(bin));
        }
        return xySeries;
    }
    private Histogram histogram() {
        return HistogramBuilder
                .with(movies())
                .title("Movies per year")
                .x("Year")
                .y("Count")
                .legend("Movies")
                .use(Movie::year);
    }

    private Stream<Movie> movies() {
        return store.movies();
    }

}
