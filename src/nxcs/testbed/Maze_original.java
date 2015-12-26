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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import nxcs.Environment;
import nxcs.NXCS;
import nxcs.NXCSParameters;
import nxcs.Reward;
import nxcs.XienceMath;


/**
 * Represents a maze problem which is loaded in from a file. Such a file should
 * contain a rectangular array of 'O', 'T' and 'F' characters. A sample is given
 * in Mazes/Woods1.txt
 */
public class Maze_original implements Environment {
	/**
	 * The raw characters in the maze
	 */
	private char[][] mazeTiles;

	/**
	 * The current position of the agent in the maze
	 */
	private int x, y;

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
	
	private List<Reward> rewardGrid;

	/**
	 * A list which maps the indices to (delta x, delta y) pairs for moving the
	 * agent around the environment
	 */
	private List<Point> actions;
	
	public List<Reward> getRewardGrid() {
		return rewardGrid;
	}

	/**
	 * The number of timesteps since the agent last discovered a final state
	 */
	private int count;

	/**
	 * Loads a maze from the given maze file
	 * 
	 * @param mazeFile
	 *            The filename of the maze to load
	 * @throws IOException
	 *             On standard IO problems
	 */
	public Maze_original(String mazeFile) throws IOException {
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
	public Maze_original(File f) throws IOException {
		// Set up the encoding table
		encodingTable = new HashMap<Character, String>();
		encodingTable.put('O', "00");
		encodingTable.put('T', "01");
		encodingTable.put(null, "10");// For out of the maze positions
		encodingTable.put('F', "11");

		openLocations = new ArrayList<Point>();
		finalStates = new ArrayList<Point>();
		rewardGrid = new ArrayList<Reward>();
		
		actions = new ArrayList<Point>();
		actions.add(new Point(-1, -1));// Up, Left
		actions.add(new Point(0, -1));// Up
		actions.add(new Point(1, -1));// Up, Right

		actions.add(new Point(-1, 0));// Left
		actions.add(new Point(1, 0));// Right

		actions.add(new Point(-1, 1));// Down, Left
		actions.add(new Point(0, 1));// Down
		actions.add(new Point(1, 1));// Down, Right

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
					rewardGrid.add(new Reward(new Point(j, i), -1, Double.parseDouble('O')));
				} else if (c == 'F') {
					finalStates.add(new Point(j, i));
				} else {
					rewardGrid.add(new Reward(new Point(j, i), -1, Double.parseDouble('c')));
				}
			}
		}
	}

	/**
	 * Resets the agent to a random open position in the environment
	 */
	private void resetPosition() {
		Point randomOpenPoint = XienceMath.choice(openLocations);
		x = randomOpenPoint.x;
		y = randomOpenPoint.y;
		count = 0;
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
		return getStringForState(x, y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getReward(String state, int action) {
		count++;
		if (isEndOfProblem(state)) {
			resetPosition();
			return 1000;
		}

		Point movement = actions.get(action);
		if (isValidPosition(x + movement.x, y + movement.y)) {
			x += movement.x;
			y += movement.y;

			if (mazeTiles[y][x] != 'T') {
				return rewardGrid;
			}
		}

		if (count > 100) {
			resetPosition();
		}

		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
//	public boolean isEndOfProblem(String state) {
//		for (Point finalState : finalStates) {
//			if (getStringForState(finalState.x, finalState.y).equals(state)) {
//				return true;
//			}
//		}
//
//		return false;
//	}
	
	public boolean isEndOfProblem(String state) {
		for (Point finalState : finalStates) {
			if (getStringForState(finalState.x, finalState.y).equals(state)) {
				//if(x==3 && y==3){
				//System.out.println(String.format("finalx,finaly:%d %d",finalState.x,finalState.y));
				return true;
				//}
				}
			}
		//System.out.println(String.format("norx,nory:%d %d",x,y));
		return false;

	}

	public static void main(String[] args) throws IOException {

		Map<Integer, Map<Integer, Double>> tempList = new HashMap<Integer, Map<Integer, Double>>();
		BufferedWriter writer = null;

		// create a temporary file
		String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		File logFile = new File(timeLog);

		writer = new BufferedWriter(new FileWriter(logFile));
		int totalCalcTimes = 1;

		try {

			for (int z = 0; z < totalCalcTimes; z++) {
				System.out.println(String.format("--------------------- begin to run %d----------------------", z));
				Map<Integer, Double> innerList = new HashMap<Integer, Double>();

				Maze_original maze = new Maze_original("data/woods101.txt");

				NXCSParameters params = new NXCSParameters();
				// Another set of parameters Woods1, Woods101

				params.N = 1600;
				params.stateLength = 16;
				params.numActions = 8;
				params.rho0 = 1000;
				params.pHash = 0.;
				params.gamma = 0.5;
				params.crossoverRate = 0.8;
				params.mutationRate = 0.04;
				params.thetaMNA = 8;
				params.thetaGA = 25;
				params.e0 = 10;
				params.thetaDel = 20;
				params.doActionSetSubsumption = false;
				params.doGASubsumption = false;

				// Maze5
				/*
				 * params.N = 5000; params.stateLength = 16; params.numActions =
				 * 8; params.rho0 = 1000; params.pHash = 0.; params.gamma =
				 * 0.7;//0.7->0.75 params.crossoverRate = 0.8;
				 * params.mutationRate = 0.01; params.thetaMNA = 8;
				 * params.thetaGA = 25;//25->50 params.e0 = 1;//why e0 so
				 * small?1->10 params.thetaDel = 20;//20->30
				 * params.doActionSetSubsumption = false; params.doGASubsumption
				 * = false;
				 */

				// NOTE: These parameters are not complete.

				NXCS nxcs = new NXCS(maze, params);
				int finalStateCount = 0;
				boolean logged = false;

				while (finalStateCount < 1500) {
					nxcs.runIteration();

					if (finalStateCount % 30 == 0 && !logged) {
						// average for z, eg.if z=2, then
						// result50=(result50[1]+restuls50[2])/z, then
						// result100, 150...800
						double result = maze.GetResult(maze, nxcs);
						innerList.put(finalStateCount, result);

						System.out.println(result);
						writer.write(String.format("%5.3f", result));
						writer.newLine();
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

				System.out.println("Trained on " + finalStateCount + " final states");
				tempList.put(z, innerList);
			} // endof z loop

			Map<Integer, Double> sumList = new HashMap<Integer, Double>();
			for (Map.Entry<Integer, Map<Integer, Double>> entry : tempList.entrySet()) {

				for (Map.Entry<Integer, Double> ientry : entry.getValue().entrySet()) {
					if (sumList.containsKey(ientry.getKey())) {
						sumList.put(ientry.getKey(), ientry.getValue() + sumList.get(ientry.getKey()));
					} else {
						sumList.put(ientry.getKey(), ientry.getValue());
					}

				}
			} // end of sum map loop

			System.out.println(String.format("------Result avg of %d------",totalCalcTimes));
			writer.write(String.format("------Result avg of %d------",totalCalcTimes));
			writer.newLine();
			Map<Integer, Double> treeMap = new TreeMap<Integer, Double>(sumList);
			for (Map.Entry<Integer, Double> entry : treeMap.entrySet()) {
				double avg = entry.getValue() / totalCalcTimes;
				System.out.println(String.format("%d\t : avg= \t%5.3f", entry.getKey(), avg));
				writer.write(String.format("%d : avg= %5.3f", entry.getKey(), avg));
				writer.newLine();
			}

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

	private double GetResult(Maze_original maze, NXCS nxcs) {
		int finalStateCount2 = 0;
		int timestamp = 0;
		while (finalStateCount2 < 200) {
			String state = maze.getState();
			int action = nxcs.classify(state);
			maze.getReward(state, action);
			if (maze.isEndOfProblem(maze.getState())) {
				maze.resetPosition();
				finalStateCount2++;
			}
			timestamp++;
		}
		return ((double) (timestamp)) / finalStateCount2;
	}

}
