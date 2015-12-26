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

import com.rits.cloning.Cloner;

import nxcs.Classifier;
import nxcs.Environment;
import nxcs.NXCS;
import nxcs.NXCSParameters;
import nxcs.Qvector;
import nxcs.Reward;
import nxcs.XienceMath;

/**
 * Represents a maze problem which is loaded in from a file. Such a file should
 * contain a rectangular array of 'O', 'T' and 'F' characters. A sample is given
 * in Mazes/Woods1.txt
 */
public class EMaze implements Environment {
	/**
	 * The raw characters in the maze
	 */
	private char[][] mazeTiles;

	/**
	 * The current position of the agent in the maze
	 */
	private int x, y;

	private double energy;

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

//	public List<Reward> getRewardGrid() {
//		return rewardGrid;
//	}

	/**
	 * The number of timesteps since the agent last discovered a final state
	 */
	private int count;

	private Cloner cloner;

	/**
	 * Loads a maze from the given maze file
	 * 
	 * @param mazeFile
	 *            The filename of the maze to load
	 * @throws IOException
	 *             On standard IO problems
	 */
	public EMaze(String mazeFile) throws IOException {
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
	public EMaze(File f) throws IOException {
		// Set up the encoding table FOR DST
		encodingTable = new HashMap<Character, String>();
		encodingTable.put('O', "000");
		encodingTable.put('T', "110");
		encodingTable.put('E', "100");// For out of the maze positions
		encodingTable.put('F', "111");

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
					// how to define rewardGrid??????
					// rewardGrid.add(new Reward(new Point(j, i), -1, 0));
				} else if (c != 'O' && c != 'T') {
					finalStates.add(new Point(j, i));
					// rewardGrid.add(new Reward(new Point(j, i), -1,
					// Character.getNumericValue(c)));
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
//	 public void resetPosition() {
//	 Point randomOpenPoint = XienceMath.choice(openLocations);
//	 x = randomOpenPoint.x;
//	 y = randomOpenPoint.y;
//	 count = 0;
//	 }

	// update start from 1,1
	public void resetPosition() {
		 Point randomOpenPoint = XienceMath.choice(openLocations);
		 x = randomOpenPoint.x;
		 y = randomOpenPoint.y;
		count = 0;
		energy = getIniEnergy(); 
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
	private String getStringForState(int x, int y, double energy) {
		StringBuilder build = new StringBuilder();
		for (int dy = -1; dy <= 1; dy++) {
			for (int dx = -1; dx <= 1; dx++) {
				if (dx == 0 && dy == 0)
					continue;
				build.append(getEncoding(x + dx, y + dy));
			}
		}
		// state of energy, used in energy maze
		if (energy > 0.5) {
			build.append(1);
		} else {
			build.append(0);
		}
		return build.toString();
	}

	

	// TODO: where to ini first energy

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
		System.out.println("x:" + x + " y:" + y + " energy:" + energy);
		return getStringForState(x, y, energy);
	}

	/**
	 * {@inheritDoc}
	 */
	public Qvector getReward(String state, int action) {

		count++;
		Qvector reward = new Qvector(-1, 0);

		Point movement = actions.get(action);
		if (isValidPosition(x + movement.x, y + movement.y)) {
			x += movement.x;
			y += movement.y;
			//energy = energy - 0.03;
		}

		if (isEndOfProblem(state)) {//TODO:Check
			if (energy >= 0.5) {
				// get food
				if (x == 2 && y == 2) {
					reward.set(1, 1000d);
					// get energy
				} else {
					reward.set(1, 1d);
				}
			}
		 else {
			if (x == 4 && y == 2) {// get energy
				reward.set(1, 1000d);
			} else {
				reward.set(1, 1d);
			}

		}}
		
		if(energy<=0){
			resetPosition();
			return new Qvector(-1, 0);
		}

		if (count > 50) {
			resetPosition();
			return new Qvector(-1, 0);
		}

		return reward;
	}

	// public double getReward(int x, int y, int energy, int action) {
	// count++;
	// int reward=0;
	// if (isEndOfProblem(x,y)) {
	// if(energy>50){
	// //get food
	// if(x==2 && y==2){
	// reward=1000;
	// //get energy
	// }else{
	// reward=1;
	// }
	// }
	// else{
	// if(x==3 && y==1){//get energy
	// reward=1000;
	// }else{
	// reward=1;
	// }
	//
	// }
	// resetPosition();
	// }
	//
	// Point movement = actions.get(action);
	// if (isValidPosition(x + movement.x, y + movement.y)) {
	// x += movement.x;
	// y += movement.y;
	// energy--;
	// }
	//
	// if (count > 100) {
	// resetPosition();
	// return 0;
	// }
	// return reward,energy;
	//
	// }

	public double getIniEnergy() {
		double e = (double) (Math.random() * (100 - 1)) + 1;
		return e / 100;
	}

	@Override
	public boolean isEndOfProblem(String state) {
		for (Point finalState : finalStates) {
			if (x == finalState.x && y == finalState.y) {
				System.out.println(String.format("finalx,finaly:%d %d", finalState.x, finalState.y));
				return true;
				// }
			}
		}
		// System.out.println(String.format("norx,nory:%d %d",x,y));
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

				EMaze maze = new EMaze("data/woods1Energy.txt");
				maze.resetPosition();
				maze.getIniEnergy();

				NXCSParameters params = new NXCSParameters();
				// Another set of parameters Woods1, Woods101

				params.N = 800;
				params.stateLength = 25;
				params.numActions = 4;
				params.rho0 = 1000;
				params.pHash = 0.;
				params.gamma = 0.5;
				params.crossoverRate = 0.8;
				params.mutationRate = 0.04;
				params.thetaMNA = 4;
				params.thetaGA = 500;
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
				int finalStateCount = 1;
				boolean logged = false;

				int i = 0;
				while (finalStateCount < 501) {
					if (i == 0) {		
						maze.energy = maze.getIniEnergy();
					}
					nxcs.runIteration(i, maze.getState());
					i++;
					System.out.println("i:"+i);

					if (finalStateCount % 500 == 0 && !logged) {
						// average for z, eg.if z=2, then
						// result50=(result50[1]+restuls50[2])/z, then
						// result100, 150...800
						System.out.println(String.format("**************", finalStateCount));
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
						//i = 0;
						// System.out.println(finalStateCount);
					}
					
					if (maze.energy<=0) {
						maze.resetPosition();
						maze.getIniEnergy();
						finalStateCount++;
						logged = false;
						//i = 0;
						// System.out.println(finalStateCount);
					}
				} // endof while

				System.out.println("Trained on " + finalStateCount + " final states");

				// print classifiers for each openlocations
				System.out.println("print classifiers for each openlocations");
				for (Point p : maze.openLocations) {
					System.out.println("x:" + p.x + " y:" + p.y);
					List<Classifier> C = nxcs.getMatchSet(maze.getStringForState(p.x, p.y, maze.energy));
					for (Classifier c : C) {
						System.out.println("clas: " + c);
					}

				}

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

			System.out.println(String.format("------Result avg of %d------", totalCalcTimes));
			writer.write(String.format("------Result avg of %d------", totalCalcTimes));
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

	private double GetResult(EMaze maze, NXCS nxcs) {
		int finalStateCount2 = 0;
		int timestamp = 0;
		while (finalStateCount2 < 20) {
			String state = maze.getState();
			int action = nxcs.classify(state);
			maze.getReward(state, action);
			if (maze.isEndOfProblem(maze.getState())) {
				maze.getIniEnergy();
				maze.resetPosition();
				finalStateCount2++;
			}
			timestamp++;
		}
		return ((double) (timestamp)) / finalStateCount2;
	}



}
