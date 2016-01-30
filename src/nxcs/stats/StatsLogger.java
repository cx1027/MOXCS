package nxcs.stats;

import java.awt.Color;
import java.awt.Paint;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class StatsLogger {

	private int xInterval;
	private int yInterval;

	public StatsLogger(int xint, int yint) {
		this.xInterval = xint;
		this.yInterval = yint;
	}

	private List<List<Snapshot>> snapshots = new ArrayList<List<Snapshot>>();

	public void logRun(List<Snapshot> stats) {
		for (int j = 0; j < stats.size(); j++) {
			if (snapshots.size() <= j) {
				snapshots.add(new ArrayList<Snapshot>());
			}
			snapshots.get(j).add(stats.get(j));
		}
	}

	public void logTrial(List<List<Snapshot>> stats) {
		for (int j = 0; j < stats.size(); j++) {
			for (int i = 0; i < stats.get(0).size(); i++) {
				if (snapshots.size() <= j) {
					snapshots.add(new ArrayList<Snapshot>());
				}
				snapshots.get(j).add(stats.get(j).get(i));
			}
		}
	}

	public List<List<Snapshot>> getStatsList() {
		return snapshots;
	}

	public void writeLogAndCSVFiles(String csvFile, String logFile, String hyperMeasure) throws IOException {
		File csv = new File(csvFile.replaceAll("<TRIAL_NUM>", "Average"));
		csv.getParentFile().mkdirs();
		FileWriter dataWriter = new FileWriter(csv);

		// Write Column Headers
		dataWriter
				.write("Number of Learning Problems, Population Size, Average Fitness, Average Specificity, Macro Classifier Proportion, "
						+ hyperMeasure + "\n");

		for (int i = 0; i < snapshots.size(); i++) {
			File finalLogFile = new File(logFile.replaceAll("<TIMESTEP_NUM>", "" + i));
			finalLogFile.getParentFile().mkdirs();
			FileWriter logWriter = new FileWriter(finalLogFile);
			List<Snapshot> stats = snapshots.get(i);
			try {
				for (int j = 0; j < stats.size(); j++) {
					Snapshot s = stats.get(j);
					logWriter.append(s.toString());
					logWriter.append("\n\n");
				}

				dataWriter.append(Snapshot.average(stats).toCSV());
			} finally {
				logWriter.close();
			}
		}
		dataWriter.close();

		for (int i = 0; i < snapshots.get(0).size(); i++) {
			csv = new File(csvFile.replaceAll("<TRIAL_NUM>", "" + (i + 1)));
			try {
				dataWriter = new FileWriter(csv);
				dataWriter
						.write("Number of Learning Problems, Population Size, Average Fitness, Average Specificity, Macro Classifier Proportion, "
								+ hyperMeasure + "\n");
				for (int j = 0; j < snapshots.size(); j++) {
					dataWriter.append(snapshots.get(j).get(i).toCSV());
				}
			} finally {
				dataWriter.close();
			}
		}
	}

	public void writeChartsAsSinglePlot(String chartFile, String problem, String performanceMeasure,
			String hyperMeasure) throws IOException {
		List<Snapshot> averages = new ArrayList<Snapshot>();
		for (List<Snapshot> s : snapshots) {
			averages.add(Snapshot.average(s));
		}

		XYSeries[] series = new XYSeries[6];
		series[0] = new XYSeries("Average Population Size");
		series[1] = new XYSeries("Average Population Fitness");
		series[2] = new XYSeries("Average Population Specificity");
		series[3] = new XYSeries("Average Macro Classifier Proportion");
		series[4] = new XYSeries(performanceMeasure);
		series[5] = new XYSeries(hyperMeasure);
		series[0].add(0, 0);
		series[1].add(0, 0);
		series[2].add(0, 0);
		series[3].add(0, 0);
		series[4].add(0, 0);
		series[5].add(0, 0);
		for (Snapshot s : averages) {
			series[0].add(s.getTimestamp(), s.getPopulationSize());
			series[1].add(s.getTimestamp(), s.getAverageFitness());
			series[2].add(s.getTimestamp(), s.getAverageSpecificity());
			series[3].add(s.getTimestamp(), s.getMacroClassifierProportion());
			series[4].add(s.getTimestamp(), s.getPerformance());
			series[5].add(s.getTimestamp(), s.getHypervolumn());
		}

		String[] labels = { "Average Population Size", "Average Classifier Fitness", "Average Classifier Specificity",
				"Macro Classifier Proportion", performanceMeasure, hyperMeasure };

		for (int i = 0; i < labels.length; i++) {
			// only plot Hyper Volumn
			if (!labels[i].equals(hyperMeasure))
				continue;

			XYSeriesCollection data = new XYSeriesCollection();
			data.addSeries(series[i]);
			JFreeChart chart = ChartFactory.createScatterPlot(labels[i] + "\n" + problem, "Number of Learning Problems",
					labels[i], data);
			chart.setBackgroundPaint(Color.white);
			XYPlot plot = chart.getXYPlot();
			plot.setBackgroundPaint(Color.white);
			plot.setOutlinePaint(Color.black);

			plot.setRangeGridlinePaint(Color.black);
			plot.setDomainGridlinePaint(Color.black);
			plot.setRenderer(new XYLineAndShapeRenderer(true, false) {
				@Override
				public Paint getItemPaint(int row, int col) {
					return Color.black;
				}
			});

			// control Y axis interval
			// NumberAxis range = (NumberAxis)plot.getRangeAxis();
			// range.setTickUnit(new NumberTickUnit(10));

			// control X axis interval
			NumberAxis domain = (NumberAxis) plot.getDomainAxis();
			domain.setTickUnit(new NumberTickUnit(this.xInterval));

			chart.removeLegend();

			File finalChartFile = new File(chartFile.replaceAll("<CHART_TITLE>", labels[i]));
			finalChartFile.getParentFile().mkdirs();
			ImageIO.write(chart.createBufferedImage(1024, 768), "png", finalChartFile);
			System.out.printf("Wrote %s with size %d%n", finalChartFile.getAbsolutePath(), finalChartFile.length());
		}
	}

	public void writeChartsAsMultiPlot(String chartFile, String problem, String[] legendNames,
			String performanceMeasure, String hyperMeasure) throws IOException {
		// snapshots.size() = number of algorithms
		XYSeries[][] series = new XYSeries[snapshots.size()][6];
		for (int i = 0; i < snapshots.size(); i++) {
			series[i][0] = new XYSeries(legendNames[i]);
			series[i][1] = new XYSeries(legendNames[i]);
			series[i][2] = new XYSeries(legendNames[i]);
			series[i][3] = new XYSeries(legendNames[i]);
			series[i][4] = new XYSeries(legendNames[i]);
			series[i][5] = new XYSeries(legendNames[i]);
		}

		for (int i = 0; i < snapshots.size(); i++) {
			series[i][0].add(0, 0);
			series[i][1].add(0, 0);
			series[i][2].add(0, 0);
			series[i][3].add(0, 0);
			series[i][4].add(0, 0);
			series[i][5].add(0, 0);
			for (Snapshot s : snapshots.get(i)) {
				series[i][0].add(s.getTimestamp(), s.getPopulationSize());
				series[i][1].add(s.getTimestamp(), s.getAverageFitness());
				series[i][2].add(s.getTimestamp(), s.getAverageSpecificity());
				series[i][3].add(s.getTimestamp(), s.getMacroClassifierProportion());
				series[i][4].add(s.getTimestamp(), s.getPerformance());
				series[i][5].add(s.getTimestamp(), s.getHypervolumn());
			}
		}

		String[] labels = { "Average Population Size", "Average Classifier Fitness", "Average Classifier Specificity",
				"Macro Classifier Proportion", performanceMeasure, hyperMeasure };

		for (int i = 0; i < labels.length; i++) {
			XYSeriesCollection data = new XYSeriesCollection();
			for (int j = 0; j < series.length; j++) {
				data.addSeries(series[j][i]);
			}
			JFreeChart chart = ChartFactory.createScatterPlot(labels[i] + "\n" + problem, "Number of Learning Problems",
					labels[i], data);
			chart.setBackgroundPaint(Color.white);
			XYPlot plot = chart.getXYPlot();
			plot.setBackgroundPaint(Color.white);
			plot.setOutlinePaint(Color.black);

			plot.setRangeGridlinePaint(Color.black);
			plot.setDomainGridlinePaint(Color.black);

			plot.setRenderer(new XYLineAndShapeRenderer(true, false));
			plot.getRenderer().setSeriesPaint(0, Color.GREEN);
			plot.getRenderer().setSeriesPaint(1, Color.BLUE);
			plot.getRenderer().setSeriesPaint(2, Color.PINK);
			plot.getRenderer().setSeriesPaint(3, Color.CYAN);

			File finalChartFile = new File(chartFile.replaceAll("<CHART_TITLE>", labels[i])).getCanonicalFile();
			finalChartFile.getParentFile().mkdirs();
			ImageIO.write(chart.createBufferedImage(640, 480), "png", finalChartFile);
			System.out.printf("Wrote %s with size %d%n", finalChartFile.getAbsolutePath(), finalChartFile.length());
		}
	}

}
