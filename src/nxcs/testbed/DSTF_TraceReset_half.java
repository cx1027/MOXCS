/*
 * use F to represent all the reward state
 */

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

import nxcs.ActionPareto;
import nxcs.Classifier;
import nxcs.Environment;
import nxcs.NXCS;
import nxcs.NXCSParameters;
import nxcs.PathStep;
import nxcs.Qvector;
import nxcs.Result;
import nxcs.Reward;
import nxcs.Trace;
import nxcs.XienceMath;

/**
 * Represents a maze problem which is loaded in from a file. Such a file should
 * contain a rectangular array of 'O', 'T' and 'F' characters. A sample is given
 * in Mazes/Woods1.txt
 */
public class DSTF_TraceReset_half implements Environment {
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
	private Map<Integer,String> encodingTable;

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
	public DSTF_TraceReset_half(String mazeFile) throws IOException {
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
	public DSTF_TraceReset_half(File f) throws IOException {
		// Set up the encoding table FOR DST
		encodingTable = new HashMap<Integer, String>();
		encodingTable.put(1, "0001");
		encodingTable.put(2, "0010");
		encodingTable.put(3, "0011");
		encodingTable.put(4, "0100");
		encodingTable.put(5, "0101");
		encodingTable.put(6, "0110");
		encodingTable.put(7, "0111");
		encodingTable.put(8, "1000");
		encodingTable.put(9, "1001");
		encodingTable.put(10, "1010");
		encodingTable.put(11, "1011");
		encodingTable.put(00, "0000");
		
		
//		encodingTable.put('O', "000");
//		encodingTable.put('T', "110");
//		encodingTable.put(null, "100");// For out of the maze positions
//		encodingTable.put('F', "111");

//		encodingTable.put('1', "001");
//		encodingTable.put('3', "011");
//		encodingTable.put('5', "101");
//		encodingTable.put('8', "010");

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
//		List<String> mazeLines = new ArrayList<String>();
//		try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
//			String line;
//			while ((line = reader.readLine()) != null) {
//				mazeLines.add(line);
//			}
//		}
//		mazeTiles = new char[mazeLines.size()][];
//		for (int i = 0; i < mazeLines.size(); i++) {
//			mazeTiles[i] = mazeLines.get(i).toCharArray();
//			if (i > 0 && mazeTiles[i].length != mazeTiles[1].length) {
//				throw new IllegalArgumentException(
//						String.format("Line %d in file %s is of different length than the others", i + 1, f.getName()));
//			}
//
//			for (int j = 0; j < mazeTiles[i].length; j++) {
//				char c = mazeTiles[i][j];
//				if (!encodingTable.containsKey(c)) {
//					throw new IllegalArgumentException(
//							String.format("Line %d in file %s has an invalid character %c", i + 1, f.getName(), c));
//				}
//
//				if (c == 'O') {
//					openLocations.add(new Point(j, i));
//					rewardGrid.add(new Reward(new Point(j, i), -1, 0));
//				} else if (c != 'O' && c != 'T') {
//					finalStates.add(new Point(j, i));
//					rewardGrid.add(new Reward(new Point(j, i), -1, Character.getNumericValue(c)));
//				}
//			}
//		}
		openLocations.add(new Point(1, 1));
		openLocations.add(new Point(2, 1));
		openLocations.add(new Point(3, 1));
		openLocations.add(new Point(4, 1));
		openLocations.add(new Point(5, 1));
		openLocations.add(new Point(6, 1));
		openLocations.add(new Point(7, 1));
		openLocations.add(new Point(8, 1));
		openLocations.add(new Point(9, 1));
		openLocations.add(new Point(10, 1));
		
		openLocations.add(new Point(2, 2));
		openLocations.add(new Point(3, 2));
		openLocations.add(new Point(4, 2));
		openLocations.add(new Point(5, 2));
		openLocations.add(new Point(6, 2));
		openLocations.add(new Point(7, 2));
		openLocations.add(new Point(8, 2));
		openLocations.add(new Point(9, 2));
		openLocations.add(new Point(10, 2));
		
		openLocations.add(new Point(3, 3));
		openLocations.add(new Point(4, 3));
		openLocations.add(new Point(5, 3));
		openLocations.add(new Point(6, 3));
		openLocations.add(new Point(7, 3));
		openLocations.add(new Point(8, 3));
		openLocations.add(new Point(9, 3));
		openLocations.add(new Point(10, 3));
		
		openLocations.add(new Point(4, 4));
		openLocations.add(new Point(5, 4));
		openLocations.add(new Point(6, 4));
		openLocations.add(new Point(7, 4));
		openLocations.add(new Point(8, 4));
		openLocations.add(new Point(9, 4));
		openLocations.add(new Point(10, 4));
		
		openLocations.add(new Point(5, 5));
		openLocations.add(new Point(6, 5));
		openLocations.add(new Point(7, 5));
		openLocations.add(new Point(8, 5));
		openLocations.add(new Point(9, 5));
		openLocations.add(new Point(10, 5));
		
		openLocations.add(new Point(6, 6));
		openLocations.add(new Point(7, 6));
		openLocations.add(new Point(8, 6));
		openLocations.add(new Point(9, 6));
		openLocations.add(new Point(10, 6));
		
		openLocations.add(new Point(7, 7));
		openLocations.add(new Point(8, 7));
		openLocations.add(new Point(9, 7));
		openLocations.add(new Point(10, 7));
		
		openLocations.add(new Point(8, 8));
		openLocations.add(new Point(9, 8));
		openLocations.add(new Point(10, 8));
		
		openLocations.add(new Point(9, 9));
		openLocations.add(new Point(10, 9));
		
		openLocations.add(new Point(10, 10));
		
		finalStates.add(new Point(1, 2));
		finalStates.add(new Point(2, 3));
		finalStates.add(new Point(3, 4));
		finalStates.add(new Point(4, 5));
		finalStates.add(new Point(5, 6));
		finalStates.add(new Point(6, 7));
		finalStates.add(new Point(7, 8));
		finalStates.add(new Point(8, 9));
		finalStates.add(new Point(9, 10));
		finalStates.add(new Point(10, 11));

		System.out.println("openLocations:" + openLocations);
		System.out.println("finalStates:" + finalStates);
		System.out.println("rewardGrid:" + rewardGrid);
	}

	/**
	 * Resets the agent to a random open position in the environment
	 */
//	 private void resetPosition() {
//	 Point randomOpenPoint = XienceMath.choice(openLocations);
//	 x = randomOpenPoint.x;
//	 y = randomOpenPoint.y;
//	 count = 0;
//	 }

	// update start from 1,1
	public void resetPosition() {
//		x = 1;
//		y = 1;
//		count = 0;
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
//	private String getEncoding(int x, int y) {
//		if (x < 0 || y < 0 || y >= mazeTiles.length || x >= mazeTiles[0].length) {
//			return encodingTable.get(null);
//		} else {
//			return encodingTable.get(mazeTiles[y][x]);
//
//		}
//	}
	private String getEncoding(int x) {
//		if (x < 0 || y < 0 || y >= mazeTiles.length || x >= mazeTiles[0].length) {
//			return encodingTable.get(null);
//		} else {
			return encodingTable.get(x);

//		}
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
//	private String getStringForState(int x, int y) {
//		StringBuilder build = new StringBuilder();
//		for (int dy = -1; dy <= 1; dy++) {
//			for (int dx = -1; dx <= 1; dx++) {
//				if (dx == 0 && dy == 0)
//					continue;
//				build.append(getEncoding(x + dx, y + dy));
//			}
//		}
//		return build.toString();
//	}
	
	private String getStringForState(int x, int y) {
		StringBuilder build = new StringBuilder();
		build.append(getEncoding(x));
		build.append(getEncoding(y));
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
		for(Point finalState : finalStates){
			if(finalState.x==x&&finalState.y==y){return true;}
		}
		for(Point openState : openLocations){
			if(openState.x==x&&openState.y==y){return true;}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getState() {
		System.out.println(String.format("x,y:%d %d", x, y));
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
		ActionPareto reward = new ActionPareto(new Qvector(-1, 0),1);

		Point movement = actions.get(action);
		if (isValidPosition(x + movement.x, y + movement.y)) {
			x += movement.x;
			y += movement.y;

//			if (mazeTiles[y][x] != 'T') {
//				for (Reward r : rewardGrid) {
//					if (r.getState().getX() == x && r.getState().getY() == y)
//						reward = r.getRewardVec();
//				}
//			}
		}
		
		
		if(x==1&&y==2){
			reward.setPareto(new Qvector(-1, 1));
			//resetPosition();
		}
		
		if(x==2&&y==3){
			reward.setPareto(new Qvector(-1, 2));
			//resetPosition();
		}
		
		if(x==3&&y==4){
			reward.setPareto(new Qvector(-1, 3));
			//resetPosition();
		}
		
		if(x==4&&y==5){
			reward.setPareto(new Qvector(-1, 5));
			//resetPosition();
		}
		
		if(x==5&&y==6){
			reward.setPareto(new Qvector(-1, 8));
			//resetPosition();
		}
		
		if(x==6&&y==7){
			reward.setPareto(new Qvector(-1, 16));
			//resetPosition();
		}
		
		if(x==7&&y==8){
			reward.setPareto(new Qvector(-1, 24));
			//resetPosition();
		}
		
		if(x==8&&y==9){
			reward.setPareto(new Qvector(-1, 50));
			//resetPosition();
		}
		
		if(x==9&&y==10){
			reward.setPareto(new Qvector(-1, 74));
			//resetPosition();
		}
		
		if(x==10&&y==11){
			reward.setPareto(new Qvector(-1, 124));
			//resetPosition();
		}

		if (count > 100) {
			resetPosition();
			System.out.println("reset_position");
			reward.setAction(5);
			reward.setPareto(new Qvector(-1, 0));
		}

		return reward;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
//	 public boolean isEndOfProblem(String state) {
//	 for (Point finalState : finalStates) {
//	 if (getStringForState(finalState.x, finalState.y).equals(state)) {
//	 return true;
//	 }
//	 }
//	
//	 return false;
//	 }

	public boolean isEndOfProblem(String state) {
		for (Point finalState : finalStates) {
			if (getStringForState(finalState.x, finalState.y).equals(state)) {
				if(finalState.x== x&&finalState.y== y){
				System.out.println(String.format("finalx,finaly:%d %d", finalState.x, finalState.y));
				return true;}
			}
		}
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

				DSTF_TraceReset_half maze = new DSTF_TraceReset_half("data/DSThalf3bit.txt");
				maze.resetPosition();

				NXCSParameters params = new NXCSParameters();
				// Another set of parameters Woods1, Woods101

				params.N = 1600;
				params.stateLength = 8;
				params.numActions = 4;
				params.rho0 = 1000;
				params.pHash = 0.;
//				params.pHash = 0.5;
				params.gamma = 0.8;
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
				Trace trace = new Trace(maze, params);
				int finalStateCount = 0;
				boolean logged = false;

				int i = 1;
				while (finalStateCount < 1001) {
					nxcs.runIteration(i, maze.getState());
					i++;

					if (finalStateCount % 1000 == 0 && !logged) {
						// average for z, eg.if z=2, then
						// result50=(result50[1]+restuls50[2])/z, then
						// result100, 150...800
//						System.out.println(String.format("org**************", finalStateCount));
//						for (Map.Entry<Point, Result> entry : maze.GetResult(maze, nxcs).entrySet()) {
//							Point key = entry.getKey();
//							Result value = entry.getValue();
//							System.out.println(String.format("Result is :(%d,%d) ", key.x, key.y) + value.toString());
//						}

//						 innerList.put(finalStateCount, result);
//						
//						 System.out.println(result);
//						 writer.write(String.format("%5.3f", result));
//						 writer.newLine();
						 logged = true;
					}

					// run function below every 50 steps
					if (maze.isEndOfProblem(maze.getState())) {
						maze.resetPosition();
						if(finalStateCount==9||finalStateCount==3||finalStateCount==5){
							System.out.println("finalStateCount:"+finalStateCount+":print classifiers for each openlocations");
							for (Point p : maze.openLocations) {
								System.out.println("x:" + p.x + " y:" + p.y);
								List<Classifier> C = nxcs.getMatchSet(maze.getStringForState(p.x, p.y));
								for (Classifier c : C) {
									System.out.println("clas: " + c);
								}

							}
						
						
						}

						finalStateCount++;
						logged = false;
						// System.out.println(finalStateCount);
					}
				} // endof while

				System.out.println("Trained on " + finalStateCount + " final states");

				// print classifiers for each openlocations
				System.out.println("print classifiers for each openlocations");
				for (Point p : maze.openLocations) {
					System.out.println("x:" + p.x + " y:" + p.y);
					List<Classifier> C = nxcs.getMatchSet(maze.getStringForState(p.x, p.y));
					for (Classifier c : C) {
						System.out.println("clas: " + c);
					}

				}
				
				System.out.println("num of classifiers"+ nxcs.getPopulation().size());
                

				int count = 30;
				System.out.println(String.format("trace**************", finalStateCount));
				for (int i1 = 0; i1 < count; i1++) {
//					maze.x=1;
//					maze.y=1;
					maze.resetPosition();
					String startState = maze.getState();
					trace.traceStart(startState, nxcs);
					i1++;
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

	// private double GetResult(DST maze, NXCS nxcs) {
	// int finalStateCount2 = 0;
	// int timestamp = 0;
	//
	// while (finalStateCount2 < 200) {
	// String state = maze.getState();
	// int action = nxcs.classify(state);
	// maze.getReward(state, action);
	// if (maze.isEndOfProblem(maze.getState())) {
	// maze.resetPosition();
	// finalStateCount2++;
	// }
	// timestamp++;
	// }
	// return ((double) (timestamp)) / finalStateCount2;
	// }

	public HashMap<Point, Result> GetResult(DSTF_TraceReset_half maze, NXCS nxcs) {
		int finalStateCount2 = 0;
		int timestamp = 0;
		HashMap<Point, Result> resultMap = new HashMap<Point, Result>();

		while (finalStateCount2 < 20) {
			PathStep pathStep = new PathStep();
			Point point = maze.getxy();
			pathStep.add(point);
			String state = maze.getState();
			int action = nxcs.classify(state);
			
			maze.getReward(state, action);

			pathStep.setStep(pathStep.getStep() + 1);

			if (maze.isEndOfProblem(state)) {
				Result rt = null;
				if (resultMap.containsKey(point)) {
					rt = resultMap.get(point);

				} else {
					rt = new Result();
					resultMap.put(point, rt);
				}
				rt.setFinailStateCount(rt.getFinailStateCount() + 1);
				rt.addPathStep(pathStep);

				maze.resetPosition();
				finalStateCount2++;
			}
			timestamp++;
		}
		return resultMap;
	}
	
	
	public HashMap<Point, Result> trace(DSTF_TraceReset_half maze, NXCS nxcs) {
		int finalStateCount2 = 0;
		int timestamp = 0;
		HashMap<Point, Result> resultMap = new HashMap<Point, Result>();

		while (finalStateCount2 < 20) {
			PathStep pathStep = new PathStep();
			Point point = maze.getxy();
			pathStep.add(point);
			String state = maze.getState();
			int action = nxcs.classify(state);
			
			maze.getReward(state, action);

			pathStep.setStep(pathStep.getStep() + 1);

			if (maze.isEndOfProblem(state)) {
				Result rt = null;
				if (resultMap.containsKey(point)) {
					rt = resultMap.get(point);

				} else {
					rt = new Result();
					resultMap.put(point, rt);
				}
				rt.setFinailStateCount(rt.getFinailStateCount() + 1);
				rt.addPathStep(pathStep);

				maze.resetPosition();
				finalStateCount2++;
			}
			timestamp++;
		}
		return resultMap;
	}

}
