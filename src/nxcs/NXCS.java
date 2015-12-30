package nxcs;

import static java.util.stream.Collectors.toCollection;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.rits.cloning.Cloner;

import nxcs.testbed.DST;
import nxcs.testbed.EMaze;

/**
 * The main class of NXCS. This class stores the data of the current state of
 * the system, as well as the environment it is operating on. We opt to provide
 * a method for users to run a single iteration of the learning process,
 * allowing more fine grained control over inter-timestep actions such as
 * logging and stopping the process.
 *
 */
public class NXCS {
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

	public List<Classifier> getPopulation() {
		return population;
	}

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
	private ActionPareto Reward;

	/**
	 * The state this system was in in the previous timestep of this system
	 */
	private String previousState;

	private addVectorNList addVL;

	private ParetoCal pareto;

	private Cloner cloner;

	private static boolean flagga;
	private static int i = 1;

	/**
	 * Constructs an NXCS instance, operating on the given environment with the
	 * given parameters
	 * 
	 * @param _env
	 *            The environment this system is to operate on
	 * @param _params
	 *            The parameters this system is to use
	 */
	public NXCS(Environment _env, NXCSParameters _params) {
		// if (_env == null)
		// throw new IllegalArgumentException("Cannot operate on null
		// environment");
		// if (_params == null)
		// throw new IllegalArgumentException("Cannot operate with null
		// parameters");

		env = _env;
		params = _params;
		population = new ArrayList<Classifier>();
		timestamp = 0;
		this.cloner = new Cloner();
		this.addVL = new addVectorNList();

	}

	public NXCS() {
		this(null, null);
	}

	/**
	 * Prints the current population of this system to stdout
	 */
	public void printPopulation() {
		for (Classifier clas : population) {
			System.out.println(clas);
		}
	}

	/**
	 * Classifies the given state using the current knowledge of the system
	 * 
	 * @param state
	 *            The state to classify
	 * @return The class the system classifies the given state into
	 */
	public int classify(String state) {
		if (state.length() != params.stateLength)
			throw new IllegalArgumentException(
					String.format("The given state (%s) is not of the correct length", state));
		List<Classifier> matchSet = population.stream().filter(c -> stateMatches(c.condition, state))
				.collect(Collectors.toList());
		double[] predictions = generatePredictions(matchSet);
		return selectAction(predictions);
	}

	// public void runIteration(int i) {
	// String state = env.getState();
	// // System.out.println("current state " + env.getState());
	// int action;
	// if (env.isEndOfProblem(state)) {
	// action = XienceMath.randomInt(params.numActions);
	//// List<Classifier> setA = updateSet(previousState, state, previousAction,
	// previousReward);
	//// System.out.println("lalallalllallallal");
	//// runGA(setA, state);
	//
	// } else {
	// List<Classifier> matchSet = generateMatchSet(state);
	// double[] predictions = generatePredictions(matchSet);
	// action = selectAction(predictions);
	// }
	//
	//
	//
	// if (previousState != null && !env.isEndOfProblem(state)) {
	// //update Colin's code, even eop, it still need to update [A]
	// //if (previousState != null && !env.isEndOfProblem(state)) {
	// List<Classifier> setA = updateSet(previousState, state, previousAction,
	// previousReward);
	// runGA(setA, state);
	// }
	//
	// // TODO:getReward to vector
	// previousReward = env.getReward(state, action);
	// System.out.println("action: " + action + " reward: " + previousReward);
	// previousAction = action;
	// previousState = state;
	// timestamp = timestamp + 1;
	// }

	public void runIteration(int finalStateCount, String previousState) {
		String prestate = env.getState();
		// System.out.println("privious state is above" + env.getState());

		int action;
		if (previousState != null) {
			List<Classifier> matchSet = generateMatchSet(previousState);
			double[] predictions = generatePredictions(matchSet);
			if (XienceMath.randomInt(params.numActions) <= -1) {
				action = selectAction(predictions);
			} else {
				action = XienceMath.randomInt(params.numActions);
			}
		} else {
			action = XienceMath.randomInt(params.numActions);
		}

		// int action;
		// if(previousState!=null){
		// List<Classifier> matchSet = generateMatchSet(previousState);
		// double[] predictions = generatePredictions(matchSet);
		// action = selectAction(predictions);
		// }else{
		// action = XienceMath.randomInt(params.numActions);
		// }

		if (i == 1) {
			action = 2;
		}
		if (i == 2) {
			action = 2;

		}
		if (i == 3) {
			action = 2;

		}
		if (i == 4) {
			action = 3;

		}
		if (i == 5) {
			action = 3;

		}
		if (i == 6) {
			action = 3;

		}
		if (i == 7) {
			action = 3;

		}
		if (i == 8) {
			action = 3;

		}
		if (i == 9) {
			action = 1;

		}
		if (i == 10) {
			action = 3;

		}
		if (i == 11) {
			action = 3;

		}
		if (i == 12) {
			action = 3;

		}
		if (i == 13) {
			action = 3;

		}
		if (i == 14) {
			action = 3;

		}
		if (i == 15) {
			action = 1;

		}
		if (i == 16) {
			action = 3;
		}
		if (i == 17) {
			action = 3;
		}
		if (i == 18) {
			action = 3;
		}
		if (i == 19) {
			action = 3;
		}
		if (i == 20) {
			action = 3;
		}
		if (i == 21) {
			action = 1;
		}
		if (i == 22) {
			action = 3;
		}
		if (i == 23) {
			action = 1;
		}
		// if (i == 23) {
		// action = 2;
		// }
		// if (i == 24) {
		// action = 3;
		// }
		// if (i == 25) {
		// action = 3;
		// }
		// if (i == 26) {
		// action = 3;
		// }
		// if (i == 27) {
		// action = 3;
		// }
		// if (i == 28) {
		// action = 2;
		// }
		// if (i == 29) {
		// action = 2;
		// }
		// if (i == 30) {
		// action = 2;
		// }
		// if (i == 31) {
		// action = 3;
		// }
		// if (i == 32) {
		// action = 3;
		// }
		// if (i == 33) {
		// action = 3;
		// }
		// if (i == 34) {
		// action = 3;
		// }
		// if (i == 35) {
		// action = 2;
		// }
		// if (i == 36) {
		// action = 2;
		// }
		// if (i == 37) {
		// action = 2;
		// }
		// if (i == 38) {
		// action = 3;
		// }
		// if (i == 39) {
		// action = 3;
		// }
		// if (i == 40) {
		// action = 3;
		// }
		// if (i == 41) {
		// action = 3;
		// }
		// if (i == 42) {
		// action = 2;
		// }
		// if (i == 43) {
		// action = 2;
		// }
		// if (i == 44) {
		// action = 2;
		// }
		// if (i == 45) {
		// action = 3;
		// }
		// if (i == 46) {
		// action = 3;
		// }
		// if (i == 47) {
		// action = 3;
		// }
		// if (i == 48) {
		// action = 3;
		// }
		// if (i == 49) {
		// action = 3;
		// }
		// if (i == 50) {
		// action = 1;
		// }
		// if (i == 51) {
		// action = 2;
		// }
		// if (i == 52) {
		// action = 2;
		// }
		// if (i == 53) {
		// action = 2;
		// }
		// if (i == 54) {
		// action = 2;
		// }
		// if (i == 55) {
		// action = 0;
		// }
		// if (i == 56) {
		// action = 0;
		// }
		// if (i == 57) {
		// action = 1;
		// }
		// if (i == 58) {
		// action = 2;
		// }
		// if (i == 59) {
		// action = 2;
		// }
		// if (i == 60) {
		// action = 2;
		// }
		// if (i == 61) {
		// action = 2;
		// }
		// if (i == 62) {
		// action = 0;
		// }
		// if (i == 63) {
		// action = 0;
		// }
		// if (i == 64) {
		// action = 1;
		// }

		// TODO:getReward to vector
		Reward = env.getReward(previousState, action);
		if (Reward.getAction() == 5) {
			previousState = null;
		}
		// System.out.println("take action: " + action + " reward: " + Reward);
		String curState = env.getState();
		// System.out.println("go to state is above" + env.getState());

		if (previousState != null) {
			// update Colin's code, even eop, it still need to update [A]
			// if (previousState != null && !env.isEndOfProblem(state)) {
			// if(finalStateCount<500){
			// List<Classifier> setA = updateSet(previousState, curState,
			// action, Reward.getPareto());
			// }

			List<Classifier> setA = updateSet(previousState, curState, action, Reward.getPareto());
			// if(previousState.equals("01010011")&&action==1&&flag==0){

			// if(finalStateCount>=500&&flag==0){
			// runGA(setA, previousState,i);
			// flag=0;
			// i++;
			// }

			// if(finalStateCount>100&&flag==0){
			flagga = runGA(setA, previousState, flagga);

			// flag=1;
			// }
			// runGA(setA, previousState);
			// System.out.println("finalStateCount:"+finalStateCount+" setA: " +
			// setA);
			//
			// }
		}

		previousAction = action;
		previousState = curState;
		timestamp = timestamp + 1;
		i++;
	}

	/**
	 * Generates a set of classifiers that match the given state. Looks first
	 * for already generates ones in the population, but if the number of
	 * matches is less than thetaMNA, generates new classifiers with random
	 * actions and adds them to the match set. Reference: Page 7 'An Algorithmic
	 * Description of XCS'
	 * 
	 * @see NXCSParameters#thetaMNA
	 * @param state
	 *            the state to generate a match set for
	 * @return The set of classifiers that match the given state
	 */
	public List<Classifier> generateMatchSet(String state) {
		assert(state != null && state.length() == params.stateLength) : "Invalid state";
		List<Classifier> setM = new ArrayList<Classifier>();
		while (setM.size() == 0) {

			setM = population.stream().filter(c -> stateMatches(c.condition, state)).collect(Collectors.toList());
			if (setM.size() < params.thetaMNA) {
				Classifier clas = generateCoveringClassifier(state, setM);
				insertIntoPopulation(clas);
				deleteFromPopulation();
				setM.clear();
			}
		}

		assert(setM.size() >= params.thetaMNA);
		// System.out.println("setM after coverubg:"+setM);
		return setM;

	}

	public List<Classifier> getMatchSet(String state) {
		return generateMatchSet(state);
	}

	public boolean actionMatches(int action, int a) {
		return action == a;
	}

	/**
	 * Deletes a random classifier in the population, with probability of being
	 * deleted proportional to the fitness of that classifier. Reference: Page
	 * 14 'An Algorithmic Description of XCS'
	 */
	private void deleteFromPopulation() {
		int numerositySum = population.stream().collect(Collectors.summingInt(c -> c.numerosity));
		if (numerositySum <= params.N) {
			return;
		}

		double averageFitness = population.stream().collect(Collectors.summingDouble(c -> c.fitness)) / numerositySum;
		double[] votes = population.stream()
				.mapToDouble(c -> c.deleteVote(averageFitness, params.thetaDel, params.delta)).toArray();
		double voteSum = Arrays.stream(votes).sum();
		votes = Arrays.stream(votes).map(d -> d / voteSum).toArray();

		Classifier choice = XienceMath.choice(population, votes);
		if (choice.numerosity > 1) {
			choice.numerosity--;
		} else {
			population.remove(choice);
		}
	}

	/**
	 * Insert the given classifier into the population, checking first to see if
	 * any classifier already in the population is more general. If a more
	 * general classifier is found with the same action, that classifiers num is
	 * incremented. Else the given classifer is added to the population.
	 * Reference: Page 13 'An Algorithmic Description of XCS'
	 * 
	 * @param classifier
	 *            The classifier to add
	 */
	private void insertIntoPopulation(Classifier clas) {
		assert(clas != null) : "Cannot insert null classifier";
		Optional<Classifier> same = population.stream()
				.filter(c -> c.action == clas.action && c.condition.equals(clas.condition)).findFirst();
		if (same.isPresent()) {
			same.get().numerosity++;
		} else {
			population.add(clas);
		}
	}

	/**
	 * Generates a classifier with the given state as the condition and a random
	 * action not covered by the given set of classifiers Reference: Page 8 'An
	 * Algorithmic Description of XCS'
	 * 
	 * @param state
	 *            The state to use as the condition for the new classifier
	 * @param matchSet
	 *            The current covering classifiers
	 * @return The generated classifier
	 */
	private Classifier generateCoveringClassifier(String state, List<Classifier> matchSet) {
		assert(state != null && matchSet != null) : "Invalid parameters";
		assert(state.length() == params.stateLength) : "Invalid state length";

		Classifier clas = new Classifier(params, state);
		Set<Integer> usedActions = matchSet.stream().map(c -> c.action).distinct().collect(Collectors.toSet());
		Set<Integer> unusedActions = IntStream.range(0, params.numActions).filter(i -> !usedActions.contains(i)).boxed()
				.collect(Collectors.toSet());
		clas.action = unusedActions.iterator().next();
		clas.timestamp = timestamp;

		return clas;

	}

	//// generate all the ParetoVVector=Q+R for curState
	// private List<ActionPareto> getParetoVVector(List<Classifier> setM) {
	// assert(setM != null && setM.size() >= params.thetaMNA) : "Invalid match
	//// set";
	// HashMap<Integer, ArrayList<Qvector>> Vdots = new HashMap<Integer,
	//// ArrayList<Qvector>>();
	// ArrayList<ActionPareto> NDdots = new ArrayList<ActionPareto>();
	//
	// // each ArrayList<Qvector> for one action
	// for (int i = 0; i < params.numActions; i++) {
	// for (Classifier clas : setM) {
	// // get different dots!!!!!!!!!!!!!!
	// final int index = i;
	// List<Classifier> setAA = setM.stream().filter(c -> c.action ==
	//// index).collect(Collectors.toList());
	// for (Classifier cl : setAA) {
	// // get V=Q+R
	// addVL = new addVectorNList();
	// cl.setV(addVL.addVectorNList(cl.getQ(), cl.getR()));
	//
	// // get the different points on pareto front
	//
	// if (Vdots.get(index) == null) {
	// ArrayList<Qvector> iniQL = new ArrayList<Qvector>(cl.getV());
	// Vdots.put(index, iniQL);
	// } else {
	// boolean flag = true;
	// for (Qvector v : cl.getV()) {
	// for (Qvector archiV : Vdots.get(index)) {
	// if (v == archiV) {
	// flag = false;
	// break;
	// }
	// }
	// if (flag == true) {
	// Vdots.get(index).add(v);
	// }
	// }
	// }
	// }
	// }
	// }
	// // get pareto for each aciton, and count the number of the Qvectors for
	// // each action
	// // transfer Vdots to ActionPareto format
	// for (int index = 0; index < params.numActions; index++) {
	// for (int i = 0; i < Vdots.get(index).size(); i++) {
	// NDdots.add(new ActionPareto(Vdots.get(index).get(i), index));
	// }
	// }
	// // Call pareto function
	// pareto = new ParetoCal();
	// List<ActionPareto> ParetoDotwithA = new ArrayList<ActionPareto>();
	// ParetoDotwithA = pareto.getPareto(NDdots);
	//// System.out.println("*****ParetoDotwithA:" + ParetoDotwithA );
	// // ArrayList<Qvector> ParetoDot = new ArrayList<Qvector>();
	// // //back to Qvector format
	// // for (ActionPareto ap:ParetoDotwithA) {
	// // ParetoDot.add(ap.getPareto());
	// // }
	// //
	//
	// return ParetoDotwithA;
	// }

	// generate all the ParetoVVector=Q+R for curState
	private List<ActionPareto> getParetoVVector(List<Classifier> setM) {
		assert(setM != null && setM.size() >= params.thetaMNA) : "Invalid match set";
		HashMap<Integer, ArrayList<Qvector>> Vdots = new HashMap<Integer, ArrayList<Qvector>>();
		ArrayList<ActionPareto> NDdots = new ArrayList<ActionPareto>();

		// each ArrayList<Qvector> for one action
		for (int act = 0; act < params.numActions; act++) {
			if (Vdots.get(act) == null) {
				ArrayList<Qvector> iniQL = new ArrayList<Qvector>();
				Vdots.put(act, iniQL);
			}

			// only the optimal vecter
			final int actIndex = act;
			List<Classifier> setAA = setM.stream().filter(c -> c.action == actIndex).collect(Collectors.toList());
			if (setAA.size() > 0) {
				try {
					Collections.sort(setAA, (a, b) -> (int) ((a.fitness - b.fitness) * 10024));
				} catch (Exception e) {
					System.out.println(String.format("sorrrrrrrrrrrt"));
				}
				Vdots.get(actIndex).addAll(setAA.get(setAA.size() - 1).getV().stream().map(d -> d.clone()).collect(toCollection(ArrayList::new)));
			}
			// Collections.sort(setAA, (a, b) -> (int) ((a.error - b.error) *
			// 1024));
			// Vdots.get(actIndex).addAll(setAA.get(0).getV());
			// }
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
		// }

		// get pareto for each aciton, and count the number of the Qvectors for
		// each action
		// transfer Vdots to ActionPareto format
		for (int index = 0; index < params.numActions; index++) {
			try {
				for (int i = 0; i < Vdots.get(index).size(); i++) {
					NDdots.add(new ActionPareto(Vdots.get(index).get(i), index));
				}
			} catch (Exception e) {
				System.out.println("Exception!!!!!" + e);
				throw e;
			}
		}
		// Call pareto function
		pareto = new ParetoCal();
		List<ActionPareto> ParetoDotwithA = new ArrayList<ActionPareto>();
		ParetoDotwithA = pareto.getPareto(NDdots);

		return ParetoDotwithA;
	}

	/**
	 * Generates a normalized prediction array from the given match set, based
	 * on the softmax function.
	 * 
	 * @param setM
	 *            The match set to use consider for these predictions
	 * @return The prediction array calculated
	 */
	private double[] generatePredictions(List<Classifier> setM) {
		assert(setM != null && setM.size() >= params.thetaMNA) : "Invalid match set";
		double[] PA = new double[params.numActions];

		List<ActionPareto> NDV = new ArrayList<ActionPareto>();
		NDV = getParetoVVector(setM);
		// Sum the policy parameter for each action
		for (int i = 0; i < params.numActions; i++) {
			for (ActionPareto act : NDV) {
				if (act.getAction() == i) {
					PA[i] += 1;
				}
			}
		}
		return PA;
	}

	/**
	 * get all the unique V of curState and count for each action
	 * 
	 * @param setM
	 * @return
	 */
	private int[] getPreofClass(List<Classifier> setM) {
		List<ActionPareto> ParetoDot = new ArrayList<ActionPareto>();
		ParetoDot = getParetoVVector(setM);

		// Calculate how many ParetoDots for each action
		// TODO:CHECK
		int[] count = new int[params.numActions];

		for (ActionPareto ap : ParetoDot) {
			count[ap.getAction()] += 1;
		}
		return count;
	}

	// //Take the exponential of each value
	// double sum = 0;
	// for(int i = 0;i < predictions.length;i ++){
	// predictions[i] = XienceMath.clamp(predictions[i], -10, 10);
	// predictions[i] = Math.exp(predictions[i]);
	// sum += predictions[i];
	// }
	//
	// //Normalize
	// for(int i = 0;i < predictions.length;i ++){
	// predictions[i] /= sum;
	// }
	//
	// assert(predictions.length == params.numActions) : "Predictions are
	// missing?";
	// assert(Math.abs(Arrays.stream(predictions).sum() - 1) <= 0.0001) :
	// "Predictions not normalized";
	//
	// return predictions;

	/**
	 * Selects an action, stochastically, using the given predictions as
	 * probabilities for each action
	 * 
	 * @param predictions
	 *            The predictions to use to select the action
	 * @return The action selected
	 */
	private int selectAction(double[] predictions) {
		return (int) XienceMath.choice(IntStream.range(0, params.numActions).boxed().toArray(), predictions);
	}

	/**
	 * Estimates the value for a state matched by the given match set
	 * 
	 * @param setM
	 *            The match set to estimate for
	 * @return The estimated maximum value of the state
	 */
	/*
	 * private double valueFunctionEstimation(List<Classifier> setM){ double[]
	 * predictions = generatePredictions(setM); Map<Integer, List<Classifier>>
	 * classifiersForActions =
	 * population.stream().collect(Collectors.groupingBy(c -> c.action)); double
	 * ret = 0; for(Map.Entry<Integer, List<Classifier>> entry :
	 * classifiersForActions.entrySet()){ double[] predictionForAction =
	 * entry.getValue().stream().mapToDouble(c -> c.prediction).toArray();
	 * double[] weights = entry.getValue().stream().mapToDouble(c ->
	 * c.fitness).toArray(); ret += predictions[entry.getKey()] *
	 * XienceMath.average(predictionForAction, weights); } return ret;
	 * 
	 * }
	 */
	// 如果prediction不改变，这个函数是计算Q'+R'得到V'
	// 然后P=r+valueFunctionEstimation
	// 考虑valueFunctionEstimation和prediction里面PA的关系
	private double valueFunctionEstimation(List<Classifier> setM) {
		double[] PA = generatePredictions(setM);
		double ret = 0;
		for (int i = 0; i < params.numActions; i++) {
			final int index = i;
			List<Classifier> setAA = setM.stream().filter(c -> c.action == index).collect(Collectors.toList());
			double fitnessSum = setAA.stream().mapToDouble(c -> c.fitness).sum();
			double predictionSum = setAA.stream().mapToDouble(c -> c.prediction * c.fitness).sum();

			if (fitnessSum != 0)
				ret += PA[i] * predictionSum / fitnessSum;
		}
		assert(!Double.isNaN(ret) && !Double.isInfinite(ret));
		return ret;
	}

	/**
	 * Updates the match set/action set of the previous state
	 * 
	 * @see NXCSParameters#gamma
	 * @see NXCSParameters#rho0
	 * @see NXCSParameters#e0
	 * @see NXCSParameters#nu
	 * @see NXCSParameters#alpha
	 * @see NXCSParameters#beta
	 * @see NXCSParameters#doActionSetSubsumption
	 * @see Classifier#averageSize
	 * @see Classifier#error
	 * @see Classifier#prediction
	 * @see Classifier#fitness
	 * @see Classifier#omega
	 * @param previousState
	 *            The previous state of the system
	 * @param currentState
	 *            The current state of the system
	 * @param preAction
	 *            The action performed in the previous state of the system
	 * @param preReward
	 *            The reward received from performing the given action in the
	 *            given previous state
	 * @return The action set of the previous state, with subsumption (possibly)
	 *         applied
	 */
	private List<Classifier> updateSet(String previousState, String currentState, int preAction, Qvector preReward) {
		List<Classifier> previousMatchSet = generateMatchSet(previousState);
		List<Classifier> curMatchSet = generateMatchSet(currentState);

		ArrayList<Qvector> P = new ArrayList<Qvector>();
		ArrayList<Qvector> V = new ArrayList<Qvector>();

		// 51-61F
//		if (previousState.equals("110110110000111110000000") && currentState.equals("110110110000110000000110")) {
//			List<Classifier> C = generateMatchSet(previousState);
//
//			List<Classifier> A = C.stream().filter(b -> b.action == 2).collect(Collectors.toList());
//			Collections.sort(A, (a, b) -> (int) ((a.fitness - b.fitness) * 10024));
//			if (A.size() >= 1) {
//				System.out.println("51:" + A);
//
//			}
//
//		}
		// 21-21
//		if (previousState.equals("110110110000110110000000") && currentState.equals("110110110000110110000000")) {
//
//			List<Classifier> C = generateMatchSet(previousState);
//
//			List<Classifier> A = C.stream().filter(b -> b.action == 2).collect(Collectors.toList());
//			Collections.sort(A, (a, b) -> (int) ((a.fitness - b.fitness) * 10024));
//			if (A.size() >= 1) {
//				// System.out.println("21:" + A.get(A.size() - 1));
//				System.out.println("21:" + A);
//			}
//
//		}
		// Calculate P, if P at the eop, then P=R of finalstate; if normal point
		// P=r+Q'+R'
		if (env.isEndOfProblem(currentState)) {
			P.add(preReward);
			V.add(preReward);
			// System.out.println("P=r=V eop:" + P);
		} else {
			// P = reward + params.gamma * getV(generateMatchSet(currentState));
			List<ActionPareto> VA = new ArrayList<ActionPareto>();
			// TODO: UPDATE getParetoVVector, get high ave dis V
			VA = getParetoVVector(generateMatchSet(currentState));
			// if(previousState.equals("110110110000110110000000")&&currentState.equals("000000110110000110000110")&&VA.size()>1){
//			if (currentState.equals("110110110000110110000000") && VA.size() > 1) {
//				List<Classifier> C = new ArrayList<Classifier>();
//				C = generateMatchSet(currentState);
//				for (int m = 0; m < 4; m++) {
//					final int act = m;
//					List<Classifier> A = C.stream().filter(b -> b.action == act).collect(Collectors.toList());
//					Collections.sort(A, (a, b) -> (int) ((a.fitness - b.fitness) * 10024));
//					System.out.println("22:" + m + ": " + A.get(A.size() - 1));
//					System.out.println("22:" + m + ": " + A);
//
//				}
//
//			}

			for (ActionPareto v : VA) {
				V.add(v.getPareto());
//				if (Math.abs(v.getPareto().get(0) * 10 % 10) > 0) {
//					System.out.println("clas.getV():" + v);
//				}
//				if ((v.getPareto().get(1) == 1) && (v.getPareto().get(0) == -1)) {
//					System.out.println("clas.getV():" + v);
//				}
			}
			// System.out.println("unique V of curState:" + V);

			P = addVL.addVectorNList(V, preReward);
			// System.out.println("P=V+r after update:" + P);
		}

		List<Classifier> actionSet = previousMatchSet.stream().filter(cl -> cl.action == preAction)
				.collect(Collectors.toList());
		int setNumerosity = actionSet.stream().mapToInt(cl -> cl.numerosity).sum();

		// Update standard parameters
		minDistance dis = new minDistance();

		for (Classifier clas : actionSet) {
			clas.experience++;
			if (clas.experience < 1. / params.beta) {
				clas.averageSize = clas.averageSize + (setNumerosity - clas.numerosity) / clas.experience;
				ArrayList<Qvector> nextQ = V.stream().map(d -> d.clone()).collect(toCollection(ArrayList::new));
				// nextQ.addAll(clas.getQ());
				// for (Qvector q : nextQ) {
				// if ((q.get(1) == 10) && (q.get(0) == -1)) {
				// System.out.println("clas.q:" + q);
				// }
				// }
				ParetoQvector paretoQ = new ParetoQvector();

				// clas.setR(preReward;

				// there are difference when calculate V, when eop, V=the value
				// of finalpoint, there is no R
				// remember the PPT, in a normal Q learning, at the finalstate,
				// it Q=R, but here, actually V=Q+R is the real Q, because it
				// calculate Q and R seperately
				if (env.isEndOfProblem(currentState)) {
					clas.setV(paretoQ.getPareto(nextQ), P);
					// if (Math.abs(clas.getR().get(0) * 10 % 10) > 0) {
					// System.out.println("clas.R after:" + clas.getR());
					// }
					// if (Math.abs(clas.getV().get(0).get(0) * 10 % 10) > 0) {
					// System.out.println("clas.getV():" + clas.getV());
					// }
				} else {
					clas.setQ(paretoQ.getPareto(nextQ));
					// clas.setQ(V;
					// if (Math.abs(clas.getR().get(0) * 10 % 10) > 0) {
					// System.out.println("clas.R after:" + clas.getR());
					// }

					clas.setR(addVL.addVector(clas.getR(),
							addVL.divideVector(addVL.minusVector(preReward, clas.getR()), clas.experience)));

					clas.setV(addVL.addVectorNList(clas.getQ(), clas.getR()), P);

				}
				// if (Math.abs(clas.Q.get(0).get(0) * 10 % 10) > 0) {
				// System.out.println("clas.Q:" + clas.Q);
				// }
				// if (Math.abs(clas.getV().get(0).get(0) * 10 % 10) > 0) {
				// System.out.println("clas.getV():" + clas.getV());
				// }
				// if (Math.abs(clas.getR().get(0) * 10 % 10) > 0) {
				// System.out.println("clas.R after:" + clas.getR());
				// }

				// clas.error = clas.error + (dis.getCoreDistance(P,
				// clas.getV()) - clas.error) / clas.experience;
				// System.out.println("error:"+clas.error);
				clas.prediction = clas.prediction
						+ (getPredictionforupdate(previousState, preAction) - clas.prediction) / clas.experience;
				// if(!P.equals(clas.getV())){
				//// System.out.println("clas.error before:" + clas.error+ "
				// P"+P+ " clas.getV()"+clas.getV()+ "dis:"+dis.getJDistance(P,
				// clas.getV())+" clas.experience"+clas.experience);
				// }
				clas.error = clas.error + (dis.getJDistance(P, clas.getV()) - clas.error) / clas.experience;
//				System.out.println(
//						"CLAS P:" + P + "CLAS V:" + clas.getV() + "CLAS DIS:" + dis.getJDistance(P, clas.getV()));
				// System.out.println("clas.error after:" + clas.error);
			} else {
				clas.averageSize = clas.averageSize + (setNumerosity - clas.numerosity) * params.beta;
				ArrayList<Qvector> nextQ = V.stream().map(d -> d.clone()).collect(toCollection(ArrayList::new));
				// nextQ.addAll(clas.getQ());
				ParetoQvector paretoQ = new ParetoQvector();

				// clas.setV( addVL.addVectorNList(clas.getQ(), clas.getR());
				if (env.isEndOfProblem(currentState)) {
					if (Math.abs(clas.getR().get(0) * 10 % 10) > 0) {
						System.out.println("clas.R:" + clas.getR());
					}
					clas.setV(paretoQ.getPareto(nextQ), P);
				} else {
					clas.setQ(paretoQ.getPareto(nextQ));
					// clas.setQ(V;
					// clas.setR(preReward;

					clas.setR(addVL.addVector(clas.getR(),
							addVL.divideVector(addVL.minusVector(preReward, clas.getR()), clas.experience)));
					clas.setV(addVL.addVectorNList(clas.getQ(), clas.getR()), P);
				}
				// if (Math.abs(clas.Q.get(0).get(0) * 10 % 10) > 0) {
				// System.out.println("clas.Q:" + clas.Q);
				// }
				// if (Math.abs(clas.getV().get(0).get(0) * 10 % 10) > 0) {
				// System.out.println("clas.getV():" + clas.getV());
				// }
				// if (Math.abs(clas.getR().get(0) * 100 % 10) > 0) {
				// System.out.println("clas.R:" + clas.getR());
				// }
				// clas.error = clas.error + (dis.getCoreDistance(P,
				// clas.getV()) - clas.error) * params.beta;
				// System.out.println("error:"+clas.error);
				clas.prediction = clas.prediction
						+ (getPredictionforupdate(previousState, preAction) - clas.prediction) * params.beta;
				if (!P.equals(clas.getV())) {
					// System.out.println("clas.error before:" + clas.error+ "
					// P"+P+ " clas.getV()"+clas.getV()+
					// "dis:"+dis.getJDistance(P, clas.getV())+"
					// params.beta"+params.beta);
				}
				clas.error = clas.error + (dis.getJDistance(P, clas.getV()) - clas.error) * params.beta;
				// System.out.println("clas.error after:" + clas.error);
			}
		}

		// Update Fitness
		Map<Classifier, Double> kappa = actionSet.stream().collect(Collectors.toMap(cl -> cl,
				cl -> (cl.error < params.e0) ? 1 : params.alpha * Math.pow(cl.error / params.e0, -params.nu)));
		double accuracySum = kappa.entrySet().stream()
				.mapToDouble(entry -> entry.getValue() * entry.getKey().numerosity).sum();
		actionSet.forEach(cl -> cl.fitness += params.beta * (kappa.get(cl) * cl.numerosity / accuracySum - cl.fitness));

		// Update Theta
		// double[] predictions = generatePredictions(previousMatchSet);
		// for(Classifier clas : previousMatchSet){
		// double stateFeature = -predictions[clas.action];
		// if(clas.action == action){
		// stateFeature = 1 - predictions[clas.action];
		// }
		//
		// clas.omega = clas.omega + (1. / params.rho0) * deltaT * stateFeature;
		// clas.omega = XienceMath.clamp(clas.omega, -10, 10);
		// }

		if (params.doActionSetSubsumption) {
			return actionSetSubsumption(actionSet);
		}
		// System.out.println("setA after update:"+actionSet);
		return actionSet;

	}

	/**
	 * get all the unique V of curState and count for each action if not eop, if
	 * eop max=1 TODO:check max for next state if not eop
	 * 
	 * @return
	 */
	private double getPredictionforupdate(String previousState, int preAction) {

		List<Classifier> previousMatchSet = generateMatchSet(previousState);
		int nP = 0;

		if (!env.isEndOfProblem(previousState)) {
			int[] NDVnum = getPreofClass(previousMatchSet);
			nP = NDVnum[preAction];
		} else {
			nP = 1;
		}
		return nP;
	}

	/**
	 * Performs an action set subsumption, subsuming the action set into the
	 * most general of the classifiers. Reference: Page 15 'An Algorithmic
	 * Description of XCS'
	 * 
	 * @param setAA
	 *            The action set to subsume
	 * @return The updated action set
	 */
	private List<Classifier> actionSetSubsumption(List<Classifier> setA) {
		Classifier cl = setA.stream().reduce(null, (cl1, cl2) -> (!cl2.couldSubsume(params.thetaSub, params.e0)) ? cl1
				: (cl1 == null) ? cl2 : (cl1.isMoreGeneral(cl2) ? cl1 : cl2));

		if (cl != null) {
			List<Classifier> toRemove = new ArrayList<Classifier>();
			for (Classifier clas : setA) {
				if (cl.isMoreGeneral(clas)) {
					cl.numerosity = cl.numerosity + clas.numerosity;
					toRemove.add(clas);
				}
			}

			setA.removeAll(toRemove);
			population.removeAll(toRemove);
		}

		return setA;
	}

	/**
	 * Runs the genetic algorithm (assuming enough time has passed) in order to
	 * make new classifiers based on the ones currently in the action set
	 * Reference: Page 11 'An Algorithmic Description of XCS'
	 * 
	 * @see NXCSParameters#thetaGA
	 * @see NXCSParameters#mu
	 * @see NXCSParameters#chi
	 * @see NXCSParameters#doGASubsumption
	 * @param currentActionSet
	 *            The current action set in this timestep
	 * @param state
	 *            The current state from the environment
	 */
	private boolean runGA(List<Classifier> setA, String state, boolean flagga) {
		assert(setA != null && state != null) : "Invalid parameters";
		// assert(setA.size() > 0) : "No action set";
		if (setA.size() == 0)
			return false;
		assert(state.length() == params.stateLength) : "Invalid state";
		if (timestamp - XienceMath.average(setA.stream().mapToDouble(cl -> cl.timestamp).toArray()) > params.thetaGA) {
			for (Classifier clas : setA) {
				clas.timestamp = timestamp;
			}

			double fitnessSum = setA.stream().mapToDouble(cl -> cl.fitness).sum();
			double[] p = setA.stream().mapToDouble(cl -> cl.fitness / fitnessSum).toArray();
			Classifier parent1 = XienceMath.choice(setA, p);
			Classifier parent2 = XienceMath.choice(setA, p);
			// Classifier child1 = parent1.deepcopy();
			// Classifier child2 = parent2.deepcopy();

			Classifier child1 = cloner.deepClone(parent1);
			child1.GLOBAL_ID++;
			child1.id = child1.GLOBAL_ID;
			Classifier child2 = cloner.deepClone(parent2);
			child2.GLOBAL_ID++;
			child2.id = child2.GLOBAL_ID;

			child1.numerosity = child2.numerosity = 1;
			child1.experience = child2.experience = 0;
			child1.initiateAfterCopied(params);
			child2.initiateAfterCopied(params);

			if (XienceMath.random() < params.crossoverRate) {
				crossover(child1, child2);
				child1.prediction = child2.prediction = (parent1.prediction + parent2.prediction) / 2;
				child1.error = child2.error = 0.25 * (parent1.error + parent2.error) / 2;
				child1.fitness = child2.fitness = 0.1 * (parent1.fitness + parent2.fitness) / 2;
			}

			Classifier[] children = new Classifier[] { child1, child2 };
			for (Classifier child : children) {
				child.mutate(state, params.mutationRate, params.numActions);
//				if (flagga == false) {
//					child.condition = "11011011000011#110000000";
//					child.action = 2;
//					System.out.println("clas:" + child);
//					flagga = true;
//				}
				// if (i==2){
				// child.condition="11011011#110000110110000";
				// child.action=2;}
				if (params.doGASubsumption) {
					if (parent1.doesSubsume(child, params.thetaSub, params.e0)) {
						parent1.numerosity++;
					} else if (parent2.doesSubsume(child, params.thetaSub, params.e0)) {
						parent2.numerosity++;
					} else {
						insertIntoPopulation(child);
					}
				} else {
					insertIntoPopulation(child);
				}
				deleteFromPopulation();
			}
		}
		return flagga;
	}

	/**
	 * Checks whether the given condition matches the given state
	 * 
	 * @param condition
	 *            The condition to check
	 * @param state
	 *            The state to check against
	 * @return if condition[i] is '#' or state[i] for all i
	 */
	public boolean stateMatches(String condition, String state) {
		assert(condition != null && condition.length() == params.stateLength) : "Invalid condition";
		assert(state != null && state.length() == params.stateLength) : "Invalid state";
		return IntStream.range(0, condition.length())
				.allMatch(i -> condition.charAt(i) == '#' || condition.charAt(i) == state.charAt(i));
	}

	/**
	 * Performs a crossover between the two given conditions, updating both.
	 * Swaps a random number of bits between the two conditions.
	 * 
	 * @see NXCSParameters#chi
	 * @param child1
	 *            The first child to cross over
	 * @param child2
	 *            The second child to cross over
	 */
	private void crossover(Classifier child1, Classifier child2) {
		assert(child1 != null && child2 != null) : "Cannot crossover null child";
		int x = XienceMath.randomInt(params.stateLength);
		int y = XienceMath.randomInt(params.stateLength);
		if (x > y) {
			int tmp = x;
			x = y;
			y = tmp;
		}

		StringBuilder child1Build = new StringBuilder();
		StringBuilder child2Build = new StringBuilder();
		for (int i = 0; i < params.stateLength; i++) {
			if (i < x || i >= y) {
				child1Build.append(child1.condition.charAt(i));
				child2Build.append(child2.condition.charAt(i));
			} else {
				child1Build.append(child2.condition.charAt(i));
				child2Build.append(child1.condition.charAt(i));
			}
		}

		child1.condition = child1Build.toString();
		child2.condition = child2Build.toString();
	}
}
