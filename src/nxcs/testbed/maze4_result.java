package nxcs.testbed;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.rits.cloning.Cloner;

import nxcs.ActionPareto;
import nxcs.Classifier;
import nxcs.Environment;
import nxcs.HyperVolumn;
import nxcs.NXCS;
import nxcs.NXCSParameters;
import nxcs.Qvector;
import nxcs.Reward;
import nxcs.Trace;
import nxcs.XienceMath;
import nxcs.addVectorNList;
import nxcs.distance.*;
import nxcs.stats.*;

/**
 * Represents a maze problem which is loaded in from a file. Such a file should
 * contain a rectangular array of 'O', 'T' and 'F' characters. A sample is given
 * in Mazes/Woods1.txt
 */
public class maze4_result implements Environment {
	/**
	 * The raw characters in the maze
	 */
	private char[][] mazeTiles;

	/**
	 * The current position of the agent in the maze
	 */
	public int x, y;

	/**
	 * The map from characters to their binary encodings used in states and
	 * conditions
	 */
	private Map<Character, String> encodingTable;

	/**
	 * A list of points representing locations we can safely move the agent to
	 */
	private List<Point> openLocations;

	/**
	 * A list of points representing the final states in the environment
	 */
	private List<Point> finalStates;

	private ArrayList<Reward> rewardGrid;

	/**
	 * A list which maps the indices to (delta x, delta y) pairs for moving the
	 * agent around the environment
	 */
	private List<Point> actions;

	public List<Reward> getRewardGrid() {
		return rewardGrid;
	}

	public static List<Integer> act = new ArrayList<Integer>();

	/**
	 * The number of timesteps since the agent last discovered a final state
	 */
	private int count;

	private Cloner cloner;

	private final static List<Snapshot> stats = new ArrayList<Snapshot>();

	/**
	 * Loads a maze from the given maze file
	 * 
	 * @param mazeFile
	 *            The filename of the maze to load
	 * @throws IOException
	 *             On standard IO problems
	 */
	public maze4_result(String mazeFile) throws IOException {
		this(new File(mazeFile));
	}

	/**
	 * Loads a maze from the given maze file
	 * 
	 * @param f
	 *            The file of the maze to load
	 * @throws IOException
	 *             On standard IO problems
	 */
	public maze4_result(File f) throws IOException {
		// Set up the encoding table FOR DST
		encodingTable = new HashMap<Character, String>();
		encodingTable.put('O', "000");
		encodingTable.put('T', "110");
		encodingTable.put(null, "100");// For out of the maze positions
		encodingTable.put('F', "111");

		// encodingTable.put('1', "001");
		// encodingTable.put('3', "011");
		// encodingTable.put('5', "101");
		// encodingTable.put('8', "010");

		openLocations = new ArrayList<Point>();
		finalStates = new ArrayList<Point>();
		rewardGrid = new ArrayList<Reward>();

		actions = new ArrayList<Point>();
		actions.add(new Point(0, -1));// Up
		actions.add(new Point(-1, 0));// Left
		actions.add(new Point(1, 0));// Right
		actions.add(new Point(0, 1));// Down
		// actions.add(new Point(-1, -1));// Up, Left
		// actions.add(new Point(1, -1));// Up, Right
		// actions.add(new Point(-1, 1));// Down, Left
		// actions.add(new Point(1, 1));// Down, Right

		// Load the maze into a char array
		List<String> mazeLines = new ArrayList<String>();
		try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
			String line;
			while ((line = reader.readLine()) != null) {
				mazeLines.add(line);
			}
		}
		mazeTiles = new char[mazeLines.size()][];
		for (int i = 0; i < mazeLines.size(); i++) {
			mazeTiles[i] = mazeLines.get(i).toCharArray();
			if (i > 0 && mazeTiles[i].length != mazeTiles[1].length) {
				throw new IllegalArgumentException(
						String.format("Line %d in file %s is of different length than the others", i + 1, f.getName()));
			}

			for (int j = 0; j < mazeTiles[i].length; j++) {
				char c = mazeTiles[i][j];
				if (!encodingTable.containsKey(c)) {
					throw new IllegalArgumentException(
							String.format("Line %d in file %s has an invalid character %c", i + 1, f.getName(), c));
				}

				if (c == 'O') {
					openLocations.add(new Point(j, i));
					rewardGrid.add(new Reward(new Point(j, i), -1, 0));
				} else if (c != 'O' && c != 'T') {
					finalStates.add(new Point(j, i));
					rewardGrid.add(new Reward(new Point(j, i), -1, Character.getNumericValue(c)));
				}
			}

		}

		System.out.println("openLocations:" + openLocations);
		System.out.println("finalStates:" + finalStates);
		System.out.println("rewardGrid:" + rewardGrid);
	}

	/**
	 * Resets the agent to a random open position in the environment
	 */
	// private void resetPosition() {
	// Point randomOpenPoint = XienceMath.choice(openLocations);
	// x = randomOpenPoint.x;
	// y = randomOpenPoint.y;
	// count = 0;
	// }

	// update start from 1,1
	public void resetPosition() {
		Point randomOpenPoint = XienceMath.choice(openLocations);
		x = randomOpenPoint.x;
		y = randomOpenPoint.y;
		count = 0;
		// x = 1;
		// y = 1;
		// count = 0;
	}

	public void resetToSamePosition(Point xy) {
		x = xy.x;
		y = xy.y;
		count = 0;
		// x = 1;
		// y = 1;
		// count = 0;
	}

	/**
	 * Returns the two-bit encoding for the given position in the maze
	 * 
	 * @param x
	 *            The x position in the maze to get
	 * @param y
	 *            The y position in the maze to get
	 * @return The two-bit encoding of the given position
	 */
	private String getEncoding(int x, int y) {
		if (x < 0 || y < 0 || y >= mazeTiles.length || x >= mazeTiles[0].length) {
			return encodingTable.get(null);
		} else {
			return encodingTable.get(mazeTiles[y][x]);

		}
	}

	/**
	 * Calculates the 16-bit state for the given position, from the 8 positions
	 * around it
	 * 
	 * @param x
	 *            The x position of the state to get the encoding for
	 * @param y
	 *            The y position of the state to get the encoding for
	 * @return The binary representation of the given state
	 */
	private String getStringForState(int x, int y) {
		StringBuilder build = new StringBuilder();
		for (int dy = -1; dy <= 1; dy++) {
			for (int dx = -1; dx <= 1; dx++) {
				if (dx == 0 && dy == 0)
					continue;
				build.append(getEncoding(x + dx, y + dy));
			}
		}
		// state of energy, used in energy maze
		// if (energy>0.5){
		// build.append(1);
		// }
		// else{
		// build.append(0);
		// }
		return build.toString();
	}

	/**
	 * Checks whether the given position is a valid position that the agent can
	 * be in in this maze. A position is valid if it is inside the bounds of the
	 * maze and is not a tree (T)
	 * 
	 * @param x
	 *            The x position to check
	 * @param y
	 *            The y position to check.
	 * @return True if the given (x, y) position is a valid position in the maze
	 */
	private boolean isValidPosition(int x, int y) {
		return !(x < 0 || y < 0 || y >= mazeTiles.length || x >= mazeTiles[0].length) && mazeTiles[y][x] != 'T';
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getState() {
		// System.out.println(String.format("x,y:%d %d", x, y));
		return getStringForState(x, y);
	}

	public Point getxy() {
		return new Point(x, y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// return reward and state????????
	public ActionPareto getReward(String state, int action) {
		count++;
		ActionPareto reward = new ActionPareto(new Qvector(-1, 0), 1);

		Point movement = actions.get(action);
		if (isValidPosition(x + movement.x, y + movement.y)) {
			x += movement.x;
			y += movement.y;

			// if (mazeTiles[y][x] != 'T') {
			// for (Reward r : rewardGrid) {
			// if (r.getState().getX() == x && r.getState().getY() == y)
			// reward = r.getRewardVec();
			// }
			// }
		}

		if (x == 6 && y == 1) {
			reward.setPareto(new Qvector(-1, 1));
			// resetPosition();
		}

		if (x == 1 && y == 6) {
			reward.setPareto(new Qvector(-1, 10));
			// resetPosition();
		}

		if (count > 100) {
			resetPosition();
			reward.setAction(5);
			reward.setPareto(new Qvector(-1, 0));
		}

		return reward;
	}

	public boolean isEndOfProblem(String state) {
		for (Point finalState : finalStates) {
			if (getStringForState(finalState.x, finalState.y).equals(state)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) throws IOException {
		String mazeName = "maze4";
		Map<Integer, Map<Integer, Double>> tempList = new HashMap<Integer, Map<Integer, Double>>();
		BufferedWriter writer = null;

		// create a temporary file
		String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());// yyyyMMdd_HHmmss
		File logFile = new File(timeLog);

		writer = new BufferedWriter(new FileWriter(logFile));
		int totalTrailTimes = 30;
		int finalStateUpperBound = 3201;

		act.add(0);
		act.add(1);
		act.add(2);
		act.add(3);

		HashMap<Point, Qvector> rewards = new HashMap<Point, Qvector>();
		rewards.put(new Point(1, 6), new Qvector(-1, 10));
		rewards.put(new Point(6, 1), new Qvector(-1, 1));
		rewards.put(new Point(-1, -1), new Qvector(-1, 0));

		try {

			// maze.resetToSamePosition(new Point(5, 1));

			// distance and exploration setting
			String[] discCalcMethods = { "MIN", "MAX", "CORE", "J" };

			String[] actionSelectionMethods = { "maxN", "maxH" };
			// String[] actionSelectionMethods = { "maxH" };

			// TODO:for different combination, LOOP for trials!!!!!!!!!!!!!!!!!!

			NXCSParameters params = new NXCSParameters();
			// Another set of parameters Woods1, Woods101

			params.N = 3000;
			params.stateLength = 24;
			params.numActions = 4;
			params.rho0 = 1000;
			params.pHash = 0.;
			params.gamma = 0.5;
			params.crossoverRate = 0.8;
			params.mutationRate = 0.04;
			params.thetaMNA = 4;
			params.thetaGA = 500;
			// params.thetaGA = 0;
			// params.e0 = 0.05;
			params.e0 = 0.05;
			params.thetaDel = 200;
			params.doActionSetSubsumption = false;
			params.doGASubsumption = false;

			int finalStateCount = 1;
			boolean logged = false;
			HyperVolumn hypervolumn = new HyperVolumn();
			PathHyperVolumnCalculator phv = new PathHyperVolumnCalculator(hypervolumn, new addVectorNList());
			int resultInterval = 30;
			int numOfChartBars = 20;
			ArrayList<Point> traceWeights = new ArrayList<Point>();
			traceWeights.add(new Point(10, 90));
			traceWeights.add(new Point(95, 5));

			// finalStateUpperBound / 20) / 10 * 10 should be 20
			int chartXInterval = ((finalStateUpperBound / numOfChartBars) > 10)
					? (finalStateUpperBound / numOfChartBars) / 10 * 10 : 10;

			for (String actionSelectionMethod : actionSelectionMethods) {
				for (String distCalcMethod : discCalcMethods) {
					params.actionSelection = actionSelectionMethod;

					if (distCalcMethod.equals("MIN")) {
						params.disCalc = new MinDistanceCalculator();
					}
					if (distCalcMethod.equals("MAX")) {
						params.disCalc = new MaxDistanceCalculator();
					}
					if (distCalcMethod.equals("CORE")) {
						params.disCalc = new CoreDistanceCalculator();
					}
					if (distCalcMethod.equals("J")) {
						params.disCalc = new JDistanceCalculator();
					}
					if ((!distCalcMethod.equals("J")) && actionSelectionMethod.equals("random")) {
						continue;
					}

					StepStatsLogger stepTrailsLogger = new StepStatsLogger(chartXInterval, 0, true, true, false);

					StatsLogger crossTrialStats = new StatsLogger(chartXInterval, 0, true, true, false);

					// System.out.println(String.format("calculate Pareto sum at
					// every %d times", resultInterval));
					for (int trailIndex = 0; trailIndex < totalTrailTimes; trailIndex++) {
						maze4_result maze = new maze4_result("data/maze4.txt");
						NXCS nxcs = new NXCS(maze, params);

						Trace trace = new Trace(maze, params);

						// reset trail status
						maze.resetPosition();
						finalStateCount = 1;

						// clear stats
						stats.clear();

						StatsLogger logger = new StatsLogger(chartXInterval, 0, true, true, false);
						StepStatsLogger stepLogger = new StepStatsLogger(chartXInterval, 0, true, true, false);

						System.out.println(
								String.format("######### begin to run of: Action:%s - Distance:%s - Trail#: %s ",
										actionSelectionMethod, distCalcMethod, trailIndex));

						// begin
						while (finalStateCount < finalStateUpperBound) {
							nxcs.runIteration(finalStateCount, maze.getState());
							nxcs.finalState = finalStateCount;
							if (((finalStateCount < 100) || (finalStateCount % resultInterval == 0)) && !logged) {
								ArrayList<ArrayList<ArrayList<StepSnapshot>>> trailStats = new ArrayList<ArrayList<ArrayList<StepSnapshot>>>();
								double hyperSum = 0;
								ArrayList<StepSnapshot> hpStats = new ArrayList<StepSnapshot>();
								for (Point weight : traceWeights) {
									ArrayList<ArrayList<StepSnapshot>> stats = maze.traceWeight(finalStateCount, maze,
											trace, nxcs, params, weight);
									trailStats.add(stats);
									hpStats.addAll(stepLogger.flatNestedArrayList(stats));

								}
								hyperSum = phv.calculateHyperVolumnForWeights(hpStats, rewards);

								// hypervolumn of this interval
								System.out.println("Method:" + actionSelectionMethod + " - " + distCalcMethod
										+ " Trail:" + trailIndex + " finalStateCount:" + finalStateCount + " Hyper:"
										+ hyperSum);

								stats.add(new Snapshot(finalStateCount, nxcs.getPopulation(), 0, 0, hyperSum));

								stepLogger.addRawStats(trailStats);
								logged = true;
							}

							// run function below every 50 steps
							if (maze.isEndOfProblem(maze.getState())) {
								maze.resetPosition();
								finalStateCount++;
								logged = false;
								// System.out.println(finalStateCount);
							}
						} // endof while

						System.out.println("Trained on " + finalStateCount + " final states - " + actionSelectionMethod
								+ " - " + distCalcMethod);

						// Plot the picture of the whole result
						logger.logRun(stats);

						crossTrialStats.logTrial(logger.getStatsList());
						try {
							logger.writeLogAndCSVFiles(String.format(
									"log/%s/csv/%s/%s - %s - Trial %d - <TRIAL_NUM>-HyperVolumn - N%d.csv", mazeName,
									"MOXCS", actionSelectionMethod, distCalcMethod, trailIndex, params.N),
									String.format(
											"log/%s/datadump/%s/%s - %s - Trail %d-<TIMESTEP_NUM> - hypervolumn - N%d.log",
											mazeName, "MOXCS", actionSelectionMethod, distCalcMethod, trailIndex,
											params.N),
									"Hyper Volumn");
							logger.writeChartsAsSinglePlot(String.format(
									"log/%s/charts/%s/%s - %s - Trail %d - <CHART_TITLE>-hypervolumn - N%d.png",
									mazeName, "MOXCS", actionSelectionMethod, distCalcMethod, trailIndex, params.N),
									String.format("%s on %s", "MOXCS", mazeName), "performance", "Hyper Volumn");
						} catch (IOException e) {
							e.printStackTrace();
						}

						// painting for each trial
						System.out.println(String.format("trace result log**************", finalStateCount));
						stepLogger.calculateMatchPercentageForWeights(maze.getOpenLocationExpectPaths());
						stepLogger.writeLogAndCSVFiles(
								String.format("log/%s/csv/%s/%s - %s - Trial %d - <TRIAL_NUM> - N%d.csv", mazeName,
										"MOXCS", actionSelectionMethod, distCalcMethod, trailIndex, params.N),
								String.format("log/%s/datadump/%s/%s - %s - Trail %d-<TIMESTEP_NUM> - N%d.log",
										mazeName, "MOXCS", actionSelectionMethod, distCalcMethod, trailIndex, params.N),
								traceWeights);
						stepLogger.writeChartsAsSinglePlot(
								String.format("log/%s/charts/%s/%s - %s - Trail %d - <CHART_TITLE> - N%d.png", mazeName,
										"MOXCS", actionSelectionMethod, distCalcMethod, trailIndex, params.N),
								String.format("%s on %s -%s -%s N=%d", "MOXCS", mazeName, actionSelectionMethod,
										distCalcMethod, params.N));

						stepTrailsLogger.addBatchStats(stepLogger.getCurrentTrailStats());
					} // endof z loop

					// painting for the avg result for 30 trials
					crossTrialStats.writeLogAndCSVFiles(
							String.format("log/%s/csv/%s/%s - %s - Trial %s - <TRIAL_NUM>-HyperVolumn - N%d.csv",
									mazeName, "MOXCS", actionSelectionMethod, distCalcMethod, "x", params.N),
							String.format("log/%s/datadump/%s/%s - %s - Trail %s-<TIMESTEP_NUM> - hypervolumn.log",
									mazeName, "MOXCS", actionSelectionMethod, distCalcMethod, "x", params.N),
							"Hyper Volumn");
					crossTrialStats.writeChartsAsSinglePlot(
							String.format("log/%s/charts/%s/%s - %s - Trail %s - HyperVolumn - <CHART_TITLE> - N%d.png",
									mazeName, "MOXCS", actionSelectionMethod, distCalcMethod, "x", params.N),
							String.format("%s on %s", "MOXCS", mazeName), "performance", "Hyper Volumn");
					stepTrailsLogger.writeAverageChartsAsSinglePlot(
							String.format("log/%s/charts/%s/%s - %s - Trail %s - <CHART_TITLE> - N%d.png", mazeName,
									"MOXCS", actionSelectionMethod, distCalcMethod, "x", params.N),
							String.format("%s on %s -%s -%s N=%d", "MOXCS", mazeName, actionSelectionMethod,
									distCalcMethod, params.N));
					System.out.println(String.format("####$##### Result of: Action:%s - Distance:%s - Trail#: %s - N%d",
							actionSelectionMethod, distCalcMethod, "x", params.N));
					writer.write(String.format("####$##### Result of: Action:%s - Distance:%s - Trail#: %s - N%d",
							actionSelectionMethod, distCalcMethod, "x", params.N));
					writer.newLine();
				} // calculator loop
			} // action selection loop

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Close the writer regardless of what happens...
				writer.close();
			} catch (Exception e) {
			}
		} // endof try
	}

	private ArrayList<ArrayList<StepSnapshot>> traceOpenLocations(int timeStamp, maze4_result maze, Trace trace,
			NXCS nxcs, NXCSParameters params) {
		// stats variables
		ArrayList<ArrayList<StepSnapshot>> locStats = new ArrayList<ArrayList<StepSnapshot>>();
		for (Point p : maze.openLocations) {
			maze.resetToSamePosition(p);
			String startState = maze.getState();
			//
			ArrayList<StepSnapshot> trc = trace.traceStartWithTwoStates(timeStamp, maze, params, nxcs, p);
			// ArrayList<StepSnapshot> trc = trace.traceStart(startState, nxcs);
			trc.stream().forEach(x -> x.setTimestamp(timeStamp));
			locStats.add(trc);
		}
		return locStats;
	}

	private ArrayList<ArrayList<StepSnapshot>> traceWeight(int timeStamp, maze4_result maze, Trace trace, NXCS nxcs,
			NXCSParameters params, Point weights) {
		// stats variables
		ArrayList<ArrayList<StepSnapshot>> locStats = new ArrayList<ArrayList<StepSnapshot>>();
		for (Point p : maze.openLocations) {
			maze.resetToSamePosition(p);
			String startState = maze.getState();
			ArrayList<StepSnapshot> trc = trace.traceStartWithWeights(timeStamp, maze, params, nxcs, p, weights);
			// ArrayList<StepSnapshot> trc = trace.traceStart(startState, nxcs);
			trc.stream().forEach(x -> x.setTimestamp(timeStamp));
			locStats.add(trc);
		}
		return locStats;
	}

	private void printOpenLocationClassifiers(int timestamp, maze4_result maze, NXCS nxcs) {
		for (Point p : maze.openLocations) {
			// System.out.println(String.format("%d\t location:%d,%d",
			// timestamp, (int) p.getX(), (int) p.getY()));
			List<Classifier> C = nxcs.getMatchSet(maze.getStringForState(p.x, p.y));
			for (int action : act) {

				List<Classifier> A = C.stream().filter(b -> b.action == action).collect(Collectors.toList());
				Collections.sort(A, new Comparator<Classifier>() {
					@Override
					public int compare(Classifier o1, Classifier o2) {
						return o1.fitness == o2.fitness ? 0 : (o1.fitness > o2.fitness ? 1 : -1);
					}
				});
			}
		} // open locations
	}

	public ArrayList<ArrayList<StepSnapshot>> getOpenLocationExpectPaths() {
		ArrayList<ArrayList<StepSnapshot>> expect = new ArrayList<ArrayList<StepSnapshot>>();
		ArrayList<StepSnapshot> e11 = new ArrayList<StepSnapshot>();
		e11.add(new StepSnapshot(new Point(1, 1), new Point(1, 6), 7));
		expect.add(e11);
		ArrayList<StepSnapshot> e21 = new ArrayList<StepSnapshot>();
		e21.add(new StepSnapshot(new Point(2, 1), new Point(1, 6), 6));
		expect.add(e21);
		ArrayList<StepSnapshot> e41 = new ArrayList<StepSnapshot>();
		e41.add(new StepSnapshot(new Point(4, 1), new Point(1, 6), 10));
		e41.add(new StepSnapshot(new Point(4, 1), new Point(6, 1), 2));
		expect.add(e41);
		ArrayList<StepSnapshot> e51 = new ArrayList<StepSnapshot>();
		e51.add(new StepSnapshot(new Point(5, 1), new Point(6, 1), 1));
		e51.add(new StepSnapshot(new Point(5, 1), new Point(1, 6), 9));
		expect.add(e51);
		ArrayList<StepSnapshot> e22 = new ArrayList<StepSnapshot>();
		e22.add(new StepSnapshot(new Point(2, 2), new Point(1, 6), 5));
		expect.add(e22);
		ArrayList<StepSnapshot> e32 = new ArrayList<StepSnapshot>();
		e32.add(new StepSnapshot(new Point(3, 2), new Point(1, 6), 6));
		expect.add(e32);
		ArrayList<StepSnapshot> e52 = new ArrayList<StepSnapshot>();
		e52.add(new StepSnapshot(new Point(5, 2), new Point(6, 1), 2));
		e52.add(new StepSnapshot(new Point(5, 2), new Point(1, 6), 8));
		expect.add(e52);
		ArrayList<StepSnapshot> e62 = new ArrayList<StepSnapshot>();
		e62.add(new StepSnapshot(new Point(6, 2), new Point(6, 1), 1));
		e62.add(new StepSnapshot(new Point(6, 2), new Point(1, 6), 9));
		expect.add(e62);
		ArrayList<StepSnapshot> e23 = new ArrayList<StepSnapshot>();
		e23.add(new StepSnapshot(new Point(2, 3), new Point(1, 6), 4));
		expect.add(e23);
		ArrayList<StepSnapshot> e43 = new ArrayList<StepSnapshot>();
		e43.add(new StepSnapshot(new Point(4, 3), new Point(6, 1), 4));
		e43.add(new StepSnapshot(new Point(4, 3), new Point(1, 6), 6));
		expect.add(e43);
		ArrayList<StepSnapshot> e53 = new ArrayList<StepSnapshot>();
		e53.add(new StepSnapshot(new Point(5, 3), new Point(6, 1), 3));
		e53.add(new StepSnapshot(new Point(5, 3), new Point(1, 6), 7));
		expect.add(e53);
		ArrayList<StepSnapshot> e14 = new ArrayList<StepSnapshot>();
		e14.add(new StepSnapshot(new Point(1, 4), new Point(1, 6), 4));
		expect.add(e14);
		ArrayList<StepSnapshot> e24 = new ArrayList<StepSnapshot>();
		e24.add(new StepSnapshot(new Point(2, 4), new Point(1, 6), 3));
		expect.add(e24);
		ArrayList<StepSnapshot> e34 = new ArrayList<StepSnapshot>();
		e34.add(new StepSnapshot(new Point(3, 4), new Point(1, 6), 4));
		expect.add(e34);
		ArrayList<StepSnapshot> e = new ArrayList<StepSnapshot>();
		e.add(new StepSnapshot(new Point(4, 4), new Point(1, 6), 5));
		expect.add(e);
		ArrayList<StepSnapshot> e54 = new ArrayList<StepSnapshot>();
		e54.add(new StepSnapshot(new Point(5, 4), new Point(6, 1), 4));
		e54.add(new StepSnapshot(new Point(5, 4), new Point(1, 6), 6));
		expect.add(e54);
		ArrayList<StepSnapshot> e64 = new ArrayList<StepSnapshot>();
		e64.add(new StepSnapshot(new Point(6, 4), new Point(6, 1), 5));
		e64.add(new StepSnapshot(new Point(6, 4), new Point(1, 6), 7));
		expect.add(e64);
		ArrayList<StepSnapshot> e25 = new ArrayList<StepSnapshot>();
		e25.add(new StepSnapshot(new Point(2, 5), new Point(1, 6), 2));
		expect.add(e25);
		ArrayList<StepSnapshot> e45 = new ArrayList<StepSnapshot>();
		e45.add(new StepSnapshot(new Point(4, 5), new Point(1, 6), 4));
		expect.add(e45);
		ArrayList<StepSnapshot> e55 = new ArrayList<StepSnapshot>();
		e55.add(new StepSnapshot(new Point(5, 5), new Point(1, 6), 5));
		expect.add(e55);
		ArrayList<StepSnapshot> e65 = new ArrayList<StepSnapshot>();
		e65.add(new StepSnapshot(new Point(6, 5), new Point(1, 6), 6));
		expect.add(e65);
		ArrayList<StepSnapshot> e26 = new ArrayList<StepSnapshot>();
		e26.add(new StepSnapshot(new Point(2, 6), new Point(1, 6), 1));
		expect.add(e26);
		ArrayList<StepSnapshot> e36 = new ArrayList<StepSnapshot>();
		e36.add(new StepSnapshot(new Point(3, 6), new Point(1, 6), 2));
		expect.add(e36);
		ArrayList<StepSnapshot> e46 = new ArrayList<StepSnapshot>();
		e46.add(new StepSnapshot(new Point(4, 6), new Point(1, 6), 3));
		expect.add(e46);
		ArrayList<StepSnapshot> e66 = new ArrayList<StepSnapshot>();
		e66.add(new StepSnapshot(new Point(6, 6), new Point(1, 6), 7));
		expect.add(e66);

		return expect;
	}
}
