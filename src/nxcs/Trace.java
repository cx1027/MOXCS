package nxcs;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.rits.cloning.Cloner;

import nxcs.stats.StepSnapshot;
import nxcs.testbed.DST_Trace;
import nxcs.testbed.maze4;
import nxcs.testbed.maze4_result;
import nxcs.distance.*;

public class Trace {
	/**
	 * The parameters of this system.
	 */
	private final NXCSParameters params;

	/**
	 * The Environment the system is acting on
	 */
	private final Environment env;

	/**
	 * The current timestamp in this system
	 */
	private int timestamp;

	/**
	 * The action performed in the previous timestep of this system
	 */
	private int previousAction;

	/**
	 * The reward received in the previous timestep of this system
	 */
	private Qvector Reward;

	/**
	 * The state this system was in in the previous timestep of this system
	 */
	private String previousState;

	private addVectorNList addVL;

	private ParetoCal pareto;

	private Cloner cloner;

	public Trace(Environment _env, NXCSParameters _params) {
		if (_env == null)
			throw new IllegalArgumentException("Cannot operate on null environment");
		if (_params == null)
			throw new IllegalArgumentException("Cannot operate with null parameters");

		env = _env;
		params = _params;
		timestamp = 0;
		this.cloner = new Cloner();
		this.addVL = new addVectorNList();

	}

	private List<ActionPareto> getParetoVnR(List<Classifier> setM) {
		assert(setM != null && setM.size() >= params.thetaMNA) : "Invalid match set";
		HashMap<Integer, ArrayList<Qvector>> Vdots = new HashMap<Integer, ArrayList<Qvector>>();
		HashMap<Integer, ArrayList<Qvector>> Rdots = new HashMap<Integer, ArrayList<Qvector>>();
		// HashMap<Integer, ArrayList<ArrayList<Qvector>>> VR = new
		// HashMap<Integer, ArrayList<ArrayList<Qvector>>>();
		ArrayList<ActionPareto> NDdots = new ArrayList<ActionPareto>();
		// Qvector R;

		// each ArrayList<Qvector> for one action
		for (int act = 0; act < params.numActions; act++) {
			if (Vdots.get(act) == null) {
				ArrayList<Qvector> iniQL = new ArrayList<Qvector>();
				ArrayList<Qvector> iniR = new ArrayList<Qvector>();
				Vdots.put(act, iniQL);
				Rdots.put(act, iniR);
			}

			final int actIndex = act;
			List<Classifier> setAA = setM.stream().filter(c -> c.action == actIndex).collect(Collectors.toList());
			if (setAA.size() > 0) {
//				Collections.sort(setAA, (a, b) -> (int) ((a.fitness - b.fitness) * 1024));
				Collections.sort(setAA, new Comparator<Classifier>() {
					@Override
					public int compare(Classifier a, Classifier b) {
						return a.fitness == b.fitness ? 0 : (a.fitness > b.fitness ? 1 : -1);
					}
				});
				
				
				Vdots.get(actIndex).addAll(setAA.get(setAA.size() - 1).getV());
				for (int i = 0; i < setAA.get(setAA.size() - 1).getV().size(); i++) {
					Rdots.get(actIndex).add(setAA.get(setAA.size() - 1).getR());
				}
			}
		}

		// ONLY select the highest fitness of 4 actions, and store in act0
		// if(setM.size()>0){
		// if (Vdots.get(0) == null) {
		// ArrayList<Qvector> iniQL = new ArrayList<Qvector>();
		// Vdots.put(0, iniQL);
		// }
		// Collections.sort(setM, (a, b) -> (int) ((a.fitness - b.fitness) *
		// 1024));
		// Vdots.get(0).addAll(setM.get(setM.size()-1).getV());
		// for (int i = 0; i < setM.get(setM.size()-1).getV().size(); i++) {
		// Rdots.get(0).add(setM.get(setM.size()-1).getR());
		// }
		// }

		// get pareto for each aciton, and count the number of the Qvectors for
		// each action
		// transfer Vdots to ActionPareto format
		for (int index = 0; index < params.numActions; index++) {
			try {
				for (int i = 0; i < Vdots.get(index).size(); i++) {
					NDdots.add(new ActionPareto(Vdots.get(index).get(i), index, Rdots.get(index).get(i)));
				}
			} catch (Exception e) {
				System.out.println("Exception!!!!!" + e);
			}
		}
		// Call pareto function
		pareto = new ParetoCal();
		List<ActionPareto> ParetoDotwithA = pareto.getPareto(NDdots);

		return ParetoDotwithA;
	}

	public ArrayList<StepSnapshot> traceStart(String startState, NXCS nxcs) {
		// int count = 0;
		// ActionPareto TA = null;
		List<ActionPareto> listVA = new ArrayList<ActionPareto>();
		// int action = 0;
		// Qvector v = new Qvector();
		// addVectorNList minus = new addVectorNList();
		// Random randomGenerator = new Random();
		// NXCS nxcs = new NXCS(env, params);
		ArrayList<StepSnapshot> locStats = new ArrayList<StepSnapshot>();

		List<Classifier> matchSet = nxcs.getPopulation().stream()
				.filter(c -> nxcs.stateMatches(c.condition, startState)).collect(Collectors.toList());
		// get paertoV---get Q of paretoV---select one Q as target
		listVA = getParetoVnR(matchSet);

		Point xy = env.getxy();
		System.out.println(String.format("XY**************" + xy));

		for (int i = 0; i < listVA.size(); i++) {
			System.out.println(String.format("trace:" + i));
			locStats.add(tracetoFinalState(i, listVA, startState, nxcs, xy));
		}

		// int i = randomGenerator.nextInt(listVA.size());

		return locStats;
	}

	public StepSnapshot tracetoFinalState(int i, List<ActionPareto> listVA, String startState, NXCS nxcs, Point xy) {
		ActionPareto TA = null;
		addVectorNList minus = new addVectorNList();
		int action = 0;
		int count = 0;
		StepSnapshot s3 = null;
		ArrayList<Point> path = new ArrayList<Point>();

		while (TA == null) {
			if (!(listVA.get(i).getPareto().equals(new Qvector(0, 0)))) {
				TA = new ActionPareto(listVA.get(i).getPareto(), listVA.get(i).getAction(), listVA.get(i).getR());

			}
			System.out.println("iiiiiiiiii:" + i);
			// i = randomGenerator.nextInt(listVA.size());
			action = TA.getAction();
			TA.setPareto(minus.minusVector(TA.getPareto(), TA.getR()));
			// take acition
			ActionPareto curStateReward = env.getReward(startState, action);

			System.out.println("****###firstTarget:" + TA);

			// DST_Trace maze = new DST_Trace("data/DST.txt");
			System.out.println("x:y:" + env.getxy());

			// main loop
			while (true && count < 50) {
				if (env.isEndOfProblem(env.getState())) {
					System.out.println("****###finalState:" + startState);
					count++;
					s3 = new StepSnapshot(xy, env.getxy(), count, path);
					// env.resetPosition();
					env.resetToSamePosition(xy);
					break;

				} else {
					// System.out.println("***before find curTargetAQ:" +
					// curTargetAQ+"curState "+curStateReward.getState());
					// Point newCurState =this.cloner.deepClone(curState);

					TA = findTarget(TA, env.getState(), nxcs);
					// System.out.println("***after find curTargetAQ:" +
					// curTargetAQ+"curState "+curStateReward.getState());
					curStateReward = env.getReward(env.getState(), TA.getAction());
					System.out.println("***move to State:" + "x:y:" + env.getxy() + "****###nextTarget:" + TA);
					count++;
					path.add(env.getxy());
				}
			}
		}
		// cannot reach to final state within 50 steps above
		if (s3 == null) {
			s3 = new StepSnapshot(xy, env.getxy(), -1, path);
		}
		return s3;
	}

	public ActionPareto findTarget(ActionPareto curtarget, String nextState, NXCS nxcs) {
		ActionPareto nextTarget = null;
		ActionPareto nextV = null;
		addVectorNList minus = new addVectorNList();

		List<ActionPareto> varList = getParetoVnR(nxcs.generateMatchSet(nextState));
		// System.out.println("***print cur VAlist:" + VAlist);
		// find less distance between nextV and curTarget
		// getMin(nextV,curTarget)
		double min = 0;
		int m = 0;
		MaxDistanceCalculator minDis = new MaxDistanceCalculator();

		for (int i = 0; i < varList.size(); i++) {

			if (i == 0) {
				min = minDis.getDisofPonit(curtarget.getPareto(), varList.get(i).getPareto());
				m = 0;
			} else {
				if (minDis.getDisofPonit(curtarget.getPareto(), varList.get(i).getPareto()) < min) {
					min = minDis.getDisofPonit(curtarget.getPareto(), varList.get(i).getPareto());
					m = i;
				}
			}
			nextV = varList.get(m);
		}

		nextTarget = new ActionPareto(minus.minusVector(nextV.getPareto(), nextV.getR()), nextV.getAction(),
				nextV.getR());

		return nextTarget;
	}




	/*
	 * trace with weights, there is no acs in this function??????
	 */
	public ArrayList<StepSnapshot> traceStartWithWeights(int timestamp, Environment maze, NXCSParameters params,
			NXCS nxcs, Point startPos, Point weights) {
//		System.out.println(String.format("start Pos:(%d,%d)", (int) startPos.getX(), (int) startPos.getY()));
		ArrayList<StepSnapshot> ret = new ArrayList<StepSnapshot>();

//		
			maze.resetToSamePosition(startPos);
//			System.out.println(String.format("Start to loop (%d,%d), Action:%d", (int) maze.x, maze.y, action));
//			if (predictions[action] > 0) {
				int loopCount = 0;
				int steps = 0;
				ArrayList<Point> path = new ArrayList<Point>();
				StepSnapshot s3 = null;
				int stepAction = -1;
                
				Point curS = maze.getxy();
				String curState = maze.getState();
				Point curr = maze.getxy();
				int action = nxcs.classify(curState,weights);
				maze.getReward(curState, action);
				// update path and step count
				path.add(maze.getxy());
				steps++;
				String currState = maze.getState();
				if (maze.isEndOfProblem(currState)) {
					path.remove(path.size() - 1);
					s3 = new StepSnapshot(startPos, maze.getxy(), steps, path);
					ret.add(s3);
//					System.out.println("\t==>" + s3.toString());
//					System.out.println("====================");
					
				}

				while (!maze.isEndOfProblem(currState) && loopCount < 50) {
					//TODO:select classifiers that exp>avgexp, smallest error
					stepAction = nxcs.classify(currState,weights);
//					System.out.println(String.format("\t\t%d timestamp, currPos:(%d,%d) \t selected action:%d",
//							timestamp, (int) curr.getX(), (int) curr.getY(), stepAction));

//					prev = maze.getxy();
//					prevState = maze.getState();

					// Take action and get reward
					maze.getReward(currState, stepAction);
					currState = maze.getState();
					curr = maze.getxy();

					// update path and step count
					path.add(curr);
					steps++;

					if (maze.isEndOfProblem(currState)) {
						path.remove(path.size() - 1);
						//collect info for each final state
						s3 = new StepSnapshot(startPos, maze.getxy(), steps, path);
						ret.add(s3);
//						System.out.println("\t==>" + s3.toString());
//						System.out.println("====================");
						break;
					}
					loopCount++;
//					System.out.println(String.format("\t\t%d loop time in trace x,y:%d,%d", loopCount,
//							(int) maze.getxy().getX(), (int) maze.getxy().getY()));
				}
				
				// cannot reach to final state within 50 steps above
				if (s3 == null) {
					s3 = new StepSnapshot(startPos, env.getxy(), -1, path);
					ret.add(s3);
//					System.out.println("====================NONONONONONONONONO==================");
				}
//			}
//		}
//		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
		// path.forEach(x->System.out.print(String.format("->(%d,%d)", (int)
		// maze.getxy().getX(), (int) maze.getxy().getY())));
		return ret;
	}

	public ArrayList<StepSnapshot> traceStartWithTwoStates(int timestamp, Environment maze, NXCSParameters params,
			NXCS nxcs, Point startPos) {
		//System.out.println(String.format("start Pos:(%d,%d)", (int) startPos.getX(), (int) startPos.getY()));
		ArrayList<StepSnapshot> ret = new ArrayList<StepSnapshot>();

		maze.resetToSamePosition(startPos);
		String currState = maze.getState();
		final String fstate = maze.getState();
		List<Classifier> matchSet = nxcs.getPopulation().stream().filter(c -> nxcs.stateMatches(c.condition, fstate))
				.collect(Collectors.toList());
		// predictions is action based(0-1-2-3), for each action check if
		// prediction[act]>0 to take action
		double[] predictions = nxcs.generatePredictions(matchSet);
		//String sp = String.format("%d", timestamp);
		//for (int ap = 0; ap < params.numActions; ap++) {
//			sp += String.format("\t%d=%3.2f", ap, predictions[ap]);
		//}
		//System.out.println(sp);

		// SELECT ACTION
		for (int action = 0; action < params.numActions; action++) {
			maze.resetToSamePosition(startPos);
//			System.out.println(String.format("Start to loop (%d,%d), Action:%d", (int) maze.x, maze.y, action));
			if (predictions[action] > 0) {
				int loopCount = 0;
				int steps = 0;
				ArrayList<Point> path = new ArrayList<Point>();
				StepSnapshot s3 = null;
				int stepAction = -1;

				Point prev = maze.getxy();
				String prevState = maze.getState();
				Point curr = maze.getxy();
				maze.getReward(currState, action);
				// update path and step count
				path.add(maze.getxy());
				steps++;
				currState = maze.getState();
				if (maze.isEndOfProblem(currState)) {
					path.remove(path.size() - 1);
					s3 = new StepSnapshot(startPos, maze.getxy(), steps, path);
					ret.add(s3);
//					System.out.println("\t==>" + s3.toString());
//					System.out.println("====================");
					continue;
				}

				while (!maze.isEndOfProblem(currState) && loopCount < 50) {
//					stepAction = nxcs.classify(timestamp, curr, currState, prev, prevState);
					stepAction = nxcs.classify(currState,prevState);

					prev = maze.getxy();
					prevState = maze.getState();

					// Take action and get reward
					maze.getReward(currState, stepAction);
					currState = maze.getState();
					curr = maze.getxy();

					// update path and step count
					path.add(curr);
					steps++;
					if (prev.equals(curr)) {
//						System.out.println("\t\tPrev:" + prev + " curr:" + curr);
						stepAction = nxcs.classify(currState, prevState);
					}

					if (maze.isEndOfProblem(currState)) {
						path.remove(path.size() - 1);
						s3 = new StepSnapshot(startPos, maze.getxy(), steps, path);
						ret.add(s3);
//						System.out.println("\t==>" + s3.toString());
//						System.out.println("====================");
						break;
					}
					loopCount++;
//					System.out.println(String.format("\t\t%d loop time in trace x,y:%d,%d", loopCount,
//							(int) maze.getxy().getX(), (int) maze.getxy().getY()));
				}
				
				// cannot reach to final state within 50 steps above
				if (s3 == null) {
					s3 = new StepSnapshot(startPos, env.getxy(), -1, path);
					ret.add(s3);
//					System.out.println("====================NONONONONONONONONO==================");
				}
			}
		}
		//System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
		// path.forEach(x->System.out.print(String.format("->(%d,%d)", (int)
		// maze.getxy().getX(), (int) maze.getxy().getY())));
		return ret;
		}

	
	
	
}
