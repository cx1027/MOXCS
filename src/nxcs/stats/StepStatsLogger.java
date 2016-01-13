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
	private ArrayList<ArrayList<StepStatsPoint>> matchStatsPoints = new ArrayList<ArrayList<StepStatsPoint>>();

	/*
	 * add stats for a batch (batch = one trace process) use this to store stats
	 * for multi batches ArrayList<(All Open Loc) ArrayList<(One Loc)
	 * ArrayList<(One Loc Path)>>
	 */
	public void addBatchStats(ArrayList<ArrayList<ArrayList<StepSnapshot>>> stats) {
		for (int j = 0; j < stats.size(); j++) {
			if (stepSnapshots.size() <= j) {
				stepSnapshots.add(new ArrayList<ArrayList<StepSnapshot>>());
			}
			stepSnapshots.get(j).addAll(stats.get(j));
		}
	}

	public void add(ArrayList<ArrayList<StepSnapshot>> stats) {
		stepSnapshots.add(stats);
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
		return new StepStatsPoint(timeStamp, matchedCnt / expectFlat.size(),
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

	public void calculateMatchPercentage(ArrayList<ArrayList<StepSnapshot>> expect) {
		ArrayList<StepSnapshot> expectFlat = flatNestedArrayList(expect);
		ArrayList<StepStatsPoint> sts = new ArrayList<StepStatsPoint>();
		for (ArrayList<ArrayList<StepSnapshot>> r : this.stepSnapshots) {
			sts.add(calculateMatchedRate(expectFlat, r));
		}
		this.matchStatsPoints.add(sts);
		for (StepStatsPoint p : sts) {
			System.out.println(p.toString());
		}
	}

	public void writeLogAndCSVFiles(String csvFile, String logFile) throws IOException {
		File csv = new File(csvFile.replaceAll("<TRIAL_NUM>", "Average"));
		csv.getParentFile().mkdirs();
		FileWriter dataWriter = new FileWriter(csv);

		// Write Column Headers
		dataWriter.write("Number of Learning Problems, Avg. Matched Rate, Avg. Coverage Rate" + "\n");
		List<StepStatsPoint> averages = new ArrayList<StepStatsPoint>();

		for (int i = 0; i < this.matchStatsPoints.size(); i++) {
			File finalLogFile = new File(logFile.replaceAll("<TIMESTEP_NUM>", "" + i));
			finalLogFile.getParentFile().mkdirs();
			FileWriter logWriter = new FileWriter(finalLogFile);
			List<StepStatsPoint> stats = this.matchStatsPoints.get(i);
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
		}
		dataWriter.close();

		csv = new File(csvFile.replaceAll("<TRIAL_NUM>", "step_log"));
		dataWriter = new FileWriter(csv);
		dataWriter.write("Number of Learning Problems, Open State, Final State, Steps, Path" + "\n");
		for (int i = 0; i < this.stepSnapshots.size(); i++) {
			try {
				ArrayList<StepSnapshot> flat = flatNestedArrayList(stepSnapshots.get(i));
				for (StepSnapshot s : flat) {
					dataWriter.append(s.toCSV());
				}
			}catch( Exception  ex){
				System.out.println(ex.getMessage());
			}
			finally {
				
			}
		}
		dataWriter.close();
	}

	public void writeChartsAsSinglePlot(String chartFile, String problem) throws IOException {
		List<StepStatsPoint> averages = new ArrayList<StepStatsPoint>();
		for (List<StepStatsPoint> s : this.matchStatsPoints) {
			averages.addAll(s);
		}

		XYSeries[] series = new XYSeries[2];
		series[0] = new XYSeries("Average Matched Rate");
		series[1] = new XYSeries("Average Coverage Rate");
		for (StepStatsPoint s : averages) {
			series[0].add(s.timestamp, s.matchedRate);
			series[1].add(s.timestamp, s.coverage);
		}

		String[] labels = { "Avg. Matched Rate", "Avg. Coverage Rate" };

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
			ImageIO.write(chart.createBufferedImage(640, 480), "png", finalChartFile);
			System.out.printf("Wrote %s with size %d%n", finalChartFile.getAbsolutePath(), finalChartFile.length());
		}
	}
}
