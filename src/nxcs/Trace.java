package nxcs;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.rits.cloning.Cloner;

import nxcs.testbed.DST_Trace;

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
	 * The current population of this system
	 */
	private final List<Classifier> population;

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
		population = new ArrayList<Classifier>();
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
				Collections.sort(setAA, (a, b) -> (int) ((a.fitness - b.fitness) * 1024));
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

	public void traceStart(String startState, NXCS nxcs) throws IOException {
		// int count = 0;
		// ActionPareto TA = null;
		List<ActionPareto> listVA = new ArrayList<ActionPareto>();
		// int action = 0;
		// Qvector v = new Qvector();
		// addVectorNList minus = new addVectorNList();
		// Random randomGenerator = new Random();
		// NXCS nxcs = new NXCS(env, params);

		List<Classifier> matchSet = nxcs.getPopulation().stream()
				.filter(c -> nxcs.stateMatches(c.condition, startState)).collect(Collectors.toList());
		// get paertoV---get Q of paretoV---select one Q as target
		listVA = getParetoVnR(matchSet);
		
		Point xy = env.getxy();
		System.out.println(String.format("XY**************"+xy));
		
		for (int i = 0; i < listVA.size(); i++) {
			System.out.println(String.format("trace:"+i));
			tracetoFinalState(i, listVA, startState, nxcs, xy);
		}

		// int i = randomGenerator.nextInt(listVA.size());

		// return TA;
	}

	public void tracetoFinalState(int i, List<ActionPareto> listVA, String startState, NXCS nxcs,Point xy) {
		ActionPareto TA = null;
		addVectorNList minus = new addVectorNList();
		int action = 0;
		int count = 0;

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
//					env.resetPosition();
					env.resetToSamePosition(xy);
					System.out.println("****###finalState:" + startState);
					count++;
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

				}
			}

		}
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
		minDistance minDis = new minDistance();

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

}
