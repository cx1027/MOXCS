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
import java.util.Optional;
import java.util.stream.Collectors;

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

	private ArrayList<ArrayList<ArrayList<ArrayList<StepSnapshot>>>> multiSnapshots = new ArrayList<ArrayList<ArrayList<ArrayList<StepSnapshot>>>>();
	private ArrayList<ArrayList<StepStatsPoint>> multiStatsPoints = new ArrayList<ArrayList<StepStatsPoint>>();

	private ArrayList<ArrayList<ArrayList<StepSnapshot>>> multiWeightSnapshots = new ArrayList<ArrayList<ArrayList<StepSnapshot>>>();

	private ArrayList<ArrayList<ArrayList<StepSnapshot>>> stepSnapshots = new ArrayList<ArrayList<ArrayList<StepSnapshot>>>();
	private ArrayList<StepStatsPoint> statsPoints = new ArrayList<StepStatsPoint>();

	/*
	 * add batch stats(batch = one trace process) for one batche: =
	 * ArrayList<(All Open Loc) of ArrayList<(One Loc) of ArrayList<(One Loc
	 * Path)>>
	 */
	public void addBatchStats(ArrayList<StepStatsPoint> stats) {
		multiStatsPoints.add(stats);
	}

	public void add(ArrayList<ArrayList<StepSnapshot>> stats) {
		stepSnapshots.add(stats);
	}

	public void addRawStats(ArrayList<ArrayList<ArrayList<StepSnapshot>>> stats) {
		for (int i = 0; i < stats.size(); i++) {
			if (this.multiSnapshots.size() <= i) {
				ArrayList<ArrayList<ArrayList<StepSnapshot>>> list = new ArrayList<ArrayList<ArrayList<StepSnapshot>>>();
				list.add(stats.get(i));
				this.multiSnapshots.add(list);
			} else {
				this.multiSnapshots.get(i).add(stats.get(i));
			}
		}
	}

	public ArrayList<ArrayList<ArrayList<StepSnapshot>>> getCurrentRawStats() {
		return stepSnapshots;
	}

	public ArrayList<StepStatsPoint> getCurrentTrailStats() {
		return this.statsPoints;
	}

	public StepStatsPoint calculateMatchedPercentage(ArrayList<ArrayList<StepSnapshot>> expect,
			ArrayList<ArrayList<StepSnapshot>> result) {
		return calculateMatchedRate(flatNestedArrayList(expect), result);
	}

	public StepStatsPoint calculateMatchedRate(ArrayList<StepSnapshot> expectFlat,
			ArrayList<ArrayList<StepSnapshot>> result) {
		double matchedCnt = 0.0;
		double cCnt = 0.0;
		double pathMatchCnt = 0.0;
		ArrayList<StepSnapshot> resultFlat = flatNestedArrayList(result);

		for (StepSnapshot item : resultFlat) {
			matchedCnt += findMatch(item, expectFlat) ? 1 : 0;
		}
		cCnt = (double) resultFlat.stream().filter(x -> x.getSteps() == -1).count();

		int timeStamp = result.get(0).get(0).getTimestamp();
		return new StepStatsPoint(timeStamp, matchedCnt / resultFlat.size(), // (matchedCnt
																				// +
																				// cCnt),
				resultFlat.size() * 1.0 / expectFlat.size());
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

	/*
	 * find any open-final state macthed with steps=-1(loop)
	 */
	public boolean findLoop(StepSnapshot item, ArrayList<StepSnapshot> list) {
		Optional<StepSnapshot> itemx = list.stream().filter(x -> x.getOpenState().equals(item.getOpenState())
				&& x.getFinalState().equals(item.getFinalState()) && (item.getSteps() == -1)).findFirst();
		return itemx.isPresent() ? true : false;
	}

	public void calculateMatchPercentage(ArrayList<ArrayList<StepSnapshot>> expect) {
		ArrayList<StepSnapshot> expectFlat = flatNestedArrayList(expect);
		ArrayList<StepStatsPoint> sts = new ArrayList<StepStatsPoint>();
		// stepSnapshots for trail1
		for (ArrayList<ArrayList<StepSnapshot>> r : this.stepSnapshots) {
			sts.add(calculateMatchedRate(expectFlat, r));
		}
		this.statsPoints = sts;
	}

	public void calculateMatchPercentageForWeights(ArrayList<ArrayList<StepSnapshot>> expect) {
		ArrayList<StepSnapshot> expectFlat = flatNestedArrayList(expect);
		ArrayList<ArrayList<StepStatsPoint>> stsList = new ArrayList<ArrayList<StepStatsPoint>>();
		// stepSnapshots for trail1
		for (ArrayList<ArrayList<ArrayList<StepSnapshot>>> rList : this.multiSnapshots) {

			ArrayList<StepStatsPoint> sts = new ArrayList<StepStatsPoint>();
			for (ArrayList<ArrayList<StepSnapshot>> r : rList) {
				sts.add(calculateMatchedRate(expectFlat, r));
			}
			stsList.add(sts);
		}

		this.statsPoints = calculateMatchPercentageList(stsList);
	}

	public ArrayList<StepStatsPoint> calculateMatchPercentageList(ArrayList<ArrayList<StepStatsPoint>> stats) {
		ArrayList<StepStatsPoint> sts = new ArrayList<StepStatsPoint>();
		for (int i = 0; i < stats.get(0).size(); i++) {
			for (int j = 0; j < stats.size(); j++) {
				if (sts.size() <= i) {
					sts.add(new StepStatsPoint(stats.get(j).get(i).timestamp, stats.get(j).get(i).matchedRate,
							stats.get(j).get(i).coverage));
				} else {
					sts.get(i).matchedRate += stats.get(j).get(i).matchedRate;
					sts.get(i).coverage += stats.get(j).get(i).coverage;
				}
			}
		}
		sts.stream().forEach(x -> {
			x.matchedRate = x.matchedRate / stats.size();
			x.coverage = x.coverage / stats.size();
		});
		return sts;
	}

	public void writeLogAndCSVFiles(String csvFile, String logFile, ArrayList<Point> weights) throws IOException {
		File csv = new File(csvFile.replaceAll("<TRIAL_NUM>", "Average-MatchRate"));
		csv.getParentFile().mkdirs();
		FileWriter dataWriter = new FileWriter(csv);

		// Write Column Headers
		dataWriter.write("Number of Learning Problems, Avg. Matched Rate, Avg. Coverage Rate" + "\n");
		List<StepStatsPoint> averages = new ArrayList<StepStatsPoint>();

		// for (int i = 0; i < this.statsPoints.size(); i++) {
		File finalLogFile = new File(logFile.replaceAll("<TIMESTEP_NUM>", ""));
		finalLogFile.getParentFile().mkdirs();
		FileWriter logWriter = new FileWriter(finalLogFile);
		List<StepStatsPoint> stats = this.statsPoints;
		try {
			for (int j = 0; j < stats.size(); j++) {
				StepStatsPoint s = stats.get(j);
				logWriter.append(s.toString());
				logWriter.append("\n\n");
				dataWriter.append(s.toCSV());
			}
		} finally {
			logWriter.close();
		}
		// }
		dataWriter.close();

		csv = new File(csvFile.replaceAll("<TRIAL_NUM>", "step_log"));
		dataWriter = new FileWriter(csv);
		dataWriter.write("Weight,Number of Learning Problems, Open State, Final State, Steps, Path" + "\n");

		for (int j = 0; j < this.multiSnapshots.size(); j++) {
			for (int i = 0; i < this.multiSnapshots.get(j).size(); i++) {
				try {
					ArrayList<StepSnapshot> flat = flatNestedArrayList(this.multiSnapshots.get(j).get(i));
					for (StepSnapshot s : flat) {
						dataWriter.append(String.format("%d-%d,", weights.get(j).x, weights.get(j).y) + s.toCSV());
					}
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				} finally {

				}
			}
		}
		dataWriter.close();
	}

	public void writeChartsAsSinglePlot(String chartFile, String problem) throws IOException {
		List<StepStatsPoint> averages = new ArrayList<StepStatsPoint>();
		averages.addAll(statsPoints);

		XYSeries[] series = new XYSeries[2];
		series[0] = new XYSeries("Average Matched Rate");
		series[1] = new XYSeries("Average Coverage Rate");
		series[0].add(0, 0);
		series[1].add(0, 0);
		for (StepStatsPoint s : averages) {
			series[0].add(s.timestamp, s.matchedRate);
			series[1].add(s.timestamp, s.coverage);
		}

		String[] labels = { "Avg. Matched Rate", "Avg. Coverage Rate" };
		writeChartsAsSinglePlot(chartFile, problem, labels, series);
	}

	private ArrayList<StepStatsPoint> calculateTrailAverage(ArrayList<ArrayList<StepStatsPoint>> multiStatsPoints) {
		ArrayList<StepStatsPoint> ret = new ArrayList<StepStatsPoint>();
		for (int i = 0; i < multiStatsPoints.get(0).size(); i++) {
			final int ix = i;
			ArrayList<StepStatsPoint> tsList = (ArrayList<StepStatsPoint>) multiStatsPoints.stream().map(x -> x.get(ix))
					.collect(Collectors.toList());

			ret.add(new StepStatsPoint(tsList.get(0).timestamp,
					tsList.stream().mapToDouble(x -> x.matchedRate).average().getAsDouble(),
					tsList.stream().mapToDouble(x -> x.coverage).average().getAsDouble()));
		}
		return ret;
	}

	public void writeAverageChartsAsSinglePlot(String chartFile, String problem) throws IOException {
		List<StepStatsPoint> averages = this.calculateTrailAverage(this.multiStatsPoints);

		File csv = new File(chartFile.replaceAll("<CHART_TITLE>", "Match Rate") + ".csv");
		csv.getParentFile().mkdirs();
		FileWriter dataWriter = new FileWriter(csv);

		// Write Column Headers
		dataWriter.write("Number of Learning Problems, Avg. Matched Rate, Avg. Coverage Rate" + "\n");

		try {
			for (int j = 0; j < averages.size(); j++) {
				StepStatsPoint s = averages.get(j);
				dataWriter.append(s.toCSV());
			}
		} finally {
			dataWriter.close();
		}

		XYSeries[] series = new XYSeries[2];
		series[0] = new XYSeries("Average Matched Rate");
		series[1] = new XYSeries("Average Coverage Rate");
		series[0].add(0, 0);
		series[1].add(0, 0);
		for (StepStatsPoint s : averages) {
			series[0].add(s.timestamp, s.matchedRate);
			series[1].add(s.timestamp, s.coverage);
		}

		String[] labels = { "Avg. Matched Rate", "Avg. Coverage Rate" };
		writeChartsAsSinglePlot(chartFile, problem, labels, series);
	}

	public void writeChartsAsSinglePlot(String chartFile, String problem, String[] labels, XYSeries[] series)
			throws IOException {

		for (int i = 0; i < labels.length; i++) {
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
			ImageIO.write(chart.createBufferedImage(1024, 748), "png", finalChartFile);
			System.out.printf("Wrote %s with size %d%n", finalChartFile.getAbsolutePath(), finalChartFile.length());
		}
	}

}
