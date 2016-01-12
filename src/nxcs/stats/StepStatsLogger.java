package nxcs.stats;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class StepStatsLogger {

	private int xInterval;
	private int yInterval;

	public StepStatsLogger(int xint, int yint) {
		this.xInterval = xint;
		this.yInterval = yint;
	}

	private ArrayList<ArrayList<ArrayList<StepSnapshot>>> stepSnapshots = new ArrayList<ArrayList<ArrayList<StepSnapshot>>>();
	private ArrayList<StepStatsPoint> matchStatsPoints = new ArrayList<StepStatsPoint>();

	public void addStats(ArrayList<ArrayList<StepSnapshot>> stats) {
		for (int j = 0; j < stats.size(); j++) {
			if (stepSnapshots.size() <= j) {
				stepSnapshots.add(new ArrayList<ArrayList<StepSnapshot>>());
			}
			stepSnapshots.get(j).add(stats.get(j));
		}
	}

	public StepStatsPoint calculateMatchedPercentage(ArrayList<ArrayList<StepSnapshot>> expect,
			ArrayList<ArrayList<StepSnapshot>> result) {
		return calculateMatchedRate(flatNestedArrayList(expect), result);
	}

	public StepStatsPoint calculateMatchedRate(ArrayList<StepSnapshot> expectFlat,
			ArrayList<ArrayList<StepSnapshot>> result) {
		double matchedCnt = 0.0;
		ArrayList<StepSnapshot> resultFlat = flatNestedArrayList(result);

		for (StepSnapshot exp : expectFlat) {
			matchedCnt += findMatch(exp, resultFlat) ? 1 : 0;
		}
		int timeStamp = result.get(0).get(0).getTimestamp();
		return new StepStatsPoint(timeStamp, matchedCnt / expectFlat.size(), resultFlat.size() / expectFlat.size());
	}

	private ArrayList<StepSnapshot> flatNestedArrayList(ArrayList<ArrayList<StepSnapshot>> data) {
		ArrayList<StepSnapshot> ret = new ArrayList<StepSnapshot>();
		for (ArrayList<StepSnapshot> a : data) {
			ret.addAll(a);
		}
		return ret;
	}

	public boolean findMatch(StepSnapshot item, ArrayList<StepSnapshot> list) {
		for (StepSnapshot s : list) {
			if (s.equals(item))
				return true;
		}
		return false;
	}

	public void calculateMatchPercentage(ArrayList<ArrayList<StepSnapshot>> expect) {
		ArrayList<StepSnapshot> expectFlat = flatNestedArrayList(expect);
		for (ArrayList<ArrayList<StepSnapshot>> r : this.stepSnapshots) {
			this.matchStatsPoints.add(calculateMatchedRate(expectFlat, r));
		}
		for (StepStatsPoint p : this.matchStatsPoints) {
			System.out.println(String.format("M:%3.2f, C:%3.2f", p.matchRate, p.coverage));
		}
	}

	// public List<StepStatsPoint>
	// calculateAvgPercentage(ArrayList<ArrayList<StepSnapshot>> expect,
	// ArrayList<ArrayList<ArrayList<StepSnapshot>>> result) {
	// List<StepStatsPoint> ret = new ArrayList<StepStatsPoint>();
	// HashMap<Integer, ArrayList<StepStatsPoint>> temp = new HashMap<Integer,
	// ArrayList<StepStatsPoint>>();
	// for (ArrayList<ArrayList<StepSnapshot>> al : result) {
	// int key = al.get(0).get(0).getTimestamp();
	// ArrayList<StepStatsPoint> list = null;
	// if (!temp.containsKey(key)) {
	// list = new ArrayList<StepStatsPoint>();
	// }
	// list.add(calculateMatchedPercentage(expect, al));
	// }
	// for (Entry<Integer, ArrayList<StepStatsPoint>> es : temp.entrySet()) {
	// ret.add(es.getKey(), getAverage(es.getValue()));
	// }
	// return ret;
	// }

	// private StepStatsPoint getAverage(List<StepStatsPoint> list) {
	// double sum = 0.0;
	// for (StepStatsPoint p : list) {
	// sum += p.percentage;
	// }
	// return new StepStatsPoint(list.get(0).timeStamp, sum / list.size());
	// }

}
// public void writeLogAndCSVFiles(String csvFile, String logFile, String
// hyperMeasure) throws IOException {
// File csv = new File(csvFile.replaceAll("<TRIAL_NUM>", "Average"));
// csv.getParentFile().mkdirs();
// FileWriter dataWriter = new FileWriter(csv);
//
// // Write Column Headers
// dataWriter
// .write("Number of Learning Problems, Open State, Final State, Steps"+ "\n");
//
// for (int i = 0; i < StepSnapshots.size(); i++) {
// File finalLogFile = new File(logFile.replaceAll("<TIMESTEP_NUM>", "" + i));
// finalLogFile.getParentFile().mkdirs();
// FileWriter logWriter = new FileWriter(finalLogFile);
// List<StepSnapshot> stats = StepSnapshots.get(i);
// try {
// for (int j = 0; j < stats.size(); j++) {
// StepSnapshot s = stats.get(j);
// logWriter.append(s.toString());
// logWriter.append("\n\n");
// }
//
//// dataWriter.append(StepSnapshot.average(stats).toCSV());
// } finally {
// logWriter.close();
// }
// }
// dataWriter.close();
//
// for (int i = 0; i < StepSnapshots.get(0).size(); i++) {
// csv = new File(csvFile.replaceAll("<TRIAL_NUM>", "" + (i + 1)));
// try {
// dataWriter = new FileWriter(csv);
// dataWriter
// .write("Number of Learning Problems, Population Size, Average Fitness,
// Average Specificity, Macro Classifier Proportion, "
// + hyperMeasure + "\n");
// for (int j = 0; j < StepSnapshots.size(); j++) {
// dataWriter.append(StepSnapshots.get(j).get(i).toCSV());
// }
// } finally {
// dataWriter.close();
// }
// }
// }

// public void writeChartsAsSinglePlot(String chartFile, String problem, String
// performanceMeasure,
// String hyperMeasure) throws IOException {
// List<StepSnapshot> averages = new ArrayList<StepSnapshot>();
// for (List<StepSnapshot> s : StepSnapshots) {
// averages.add(StepSnapshot.average(s));
// }
//
// XYSeries[] series = new XYSeries[6];
// series[0] = new XYSeries("Average Population Size");
// series[1] = new XYSeries("Average Population Fitness");
// series[2] = new XYSeries("Average Population Specificity");
// series[3] = new XYSeries("Average Macro Classifier Proportion");
// series[4] = new XYSeries(performanceMeasure);
// series[5] = new XYSeries(hyperMeasure);
// for (StepSnapshot s : averages) {
// series[0].add(s.getTimestamp(), s.getPopulationSize());
// series[1].add(s.getTimestamp(), s.getAverageFitness());
// series[2].add(s.getTimestamp(), s.getAverageSpecificity());
// series[3].add(s.getTimestamp(), s.getMacroClassifierProportion());
// series[4].add(s.getTimestamp(), s.getPerformance());
// series[5].add(s.getTimestamp(), s.getHypervolumn());
// }
//
// String[] labels = { "Average Population Size", "Average Classifier Fitness",
// "Average Classifier Specificity",
// "Macro Classifier Proportion", performanceMeasure, hyperMeasure };
//
// for (int i = 0; i < labels.length; i++) {
// XYSeriesCollection data = new XYSeriesCollection();
// data.addSeries(series[i]);
// JFreeChart chart = ChartFactory.createScatterPlot(labels[i] + "\n" + problem,
// "Number of Learning Problems",
// labels[i], data);
// chart.setBackgroundPaint(Color.white);
// XYPlot plot = chart.getXYPlot();
// plot.setBackgroundPaint(Color.white);
// plot.setOutlinePaint(Color.black);
//
// plot.setRangeGridlinePaint(Color.black);
// plot.setDomainGridlinePaint(Color.black);
// plot.setRenderer(new XYLineAndShapeRenderer(true, false) {
// @Override
// public Paint getItemPaint(int row, int col) {
// return Color.black;
// }
// });
//
// // control Y axis interval
// // NumberAxis range = (NumberAxis)plot.getRangeAxis();
// // range.setTickUnit(new NumberTickUnit(10));
//
// // control X axis interval
// NumberAxis domain = (NumberAxis) plot.getDomainAxis();
// domain.setTickUnit(new NumberTickUnit(this.xInterval));
//
// chart.removeLegend();
//
// File finalChartFile = new File(chartFile.replaceAll("<CHART_TITLE>",
// labels[i]));
// finalChartFile.getParentFile().mkdirs();
// ImageIO.write(chart.createBufferedImage(640, 480), "png", finalChartFile);
// System.out.printf("Wrote %s with size %d%n",
// finalChartFile.getAbsolutePath(), finalChartFile.length());
// }
// }

// public void writeChartsAsMultiPlot(String chartFile, String problem, String[]
// legendNames,
// String performanceMeasure, String hyperMeasure) throws IOException {
// // StepSnapshots.size() = number of algorithms
// XYSeries[][] series = new XYSeries[StepSnapshots.size()][6];
// for (int i = 0; i < StepSnapshots.size(); i++) {
// series[i][0] = new XYSeries(legendNames[i]);
// series[i][1] = new XYSeries(legendNames[i]);
// series[i][2] = new XYSeries(legendNames[i]);
// series[i][3] = new XYSeries(legendNames[i]);
// series[i][4] = new XYSeries(legendNames[i]);
// series[i][5] = new XYSeries(legendNames[i]);
// }
//
// for (int i = 0; i < StepSnapshots.size(); i++) {
// for (StepSnapshot s : StepSnapshots.get(i)) {
// series[i][0].add(s.getTimestamp(), s.getPopulationSize());
// series[i][1].add(s.getTimestamp(), s.getAverageFitness());
// series[i][2].add(s.getTimestamp(), s.getAverageSpecificity());
// series[i][3].add(s.getTimestamp(), s.getMacroClassifierProportion());
// series[i][4].add(s.getTimestamp(), s.getPerformance());
// series[i][5].add(s.getTimestamp(), s.getHypervolumn());
// }
// }
//
// String[] labels = { "Average Population Size", "Average Classifier Fitness",
// "Average Classifier Specificity",
// "Macro Classifier Proportion", performanceMeasure, hyperMeasure };
//
// for (int i = 0; i < labels.length; i++) {
// XYSeriesCollection data = new XYSeriesCollection();
// for (int j = 0; j < series.length; j++) {
// data.addSeries(series[j][i]);
// }
// JFreeChart chart = ChartFactory.createScatterPlot(labels[i] + "\n" + problem,
// "Number of Learning Problems",
// labels[i], data);
// chart.setBackgroundPaint(Color.white);
// XYPlot plot = chart.getXYPlot();
// plot.setBackgroundPaint(Color.white);
// plot.setOutlinePaint(Color.black);
//
// plot.setRangeGridlinePaint(Color.black);
// plot.setDomainGridlinePaint(Color.black);
//
// plot.setRenderer(new XYLineAndShapeRenderer(true, false));
// plot.getRenderer().setSeriesPaint(0, Color.GREEN);
// plot.getRenderer().setSeriesPaint(1, Color.BLUE);
// plot.getRenderer().setSeriesPaint(2, Color.PINK);
// plot.getRenderer().setSeriesPaint(3, Color.CYAN);
//
// File finalChartFile = new File(chartFile.replaceAll("<CHART_TITLE>",
// labels[i])).getCanonicalFile();
// finalChartFile.getParentFile().mkdirs();
// ImageIO.write(chart.createBufferedImage(640, 480), "png", finalChartFile);
// System.out.printf("Wrote %s with size %d%n",
// finalChartFile.getAbsolutePath(), finalChartFile.length());
// }
// }
/*
 * calculate average values of trails
 */
// public static StepSnapshot average(List<StepSnapshot> snapshots) {
// // Snapshot(int popSize, double avFitness, int stamp, double hyper)
// if (snapshots.size() == 0) {
// return new StepSnapshot(new Point(0,0), new Point(0,0), 0,null);
// }
// double avPopSize = 0;
// double avFitness = 0;
// double avSpec = 0;
// double avMacroProp = 0;
// double perf = 0;
// double hyperv = 0;
//
// for (StepSnapshot snapshot : snapshots) {
// avPopSize += snapshot.populationSize;
// avFitness += snapshot.averageFitness;
// avSpec += snapshot.averageSpecificity;
// avMacroProp += snapshot.macroClassifierProportion;
// perf += snapshot.performance;
// hyperv += snapshot.hypervolumn;
// }
//
// int size = snapshots.size();
// // return new Snapshot((int)(avPopSize / size), perf / size, avMacroProp
// // / size, avFitness / size, avSpec / size, snapshots.get(0).time);
// return new Snapshot((int) (avPopSize / size), avFitness / size,
// snapshots.get(0).time, hyperv / size);
// }
// }
