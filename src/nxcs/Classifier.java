package nxcs;

import static java.util.stream.Collectors.toCollection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import nxcs.distance.*;

//import xcs.testbed.Range;

/**
 * A classifier in the NXCS system. Note that this is only a small change from a
 * classifier in XCS in that we have only added the `theta` field below. Note
 * that most of the methods in this class have a default access modifier -
 * package private for nicer encapsulation.
 *
 */
public class Classifier implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The global ID of classifiers. This is used to give each classifier an
	 * individual "name"
	 */
	public static int GLOBAL_ID = 1;

	/**
	 * The ID of this classifier
	 */
	public int id;

	/**
	 * The action this classifier recommends
	 */
	public int action;

	/**
	 * The reward prediction of a classifier
	 */
	public double prediction;

	/**
	 * The reward error prediction of a classifier
	 */
	public double error;

	/**
	 * The fitness of the classifier
	 */
	public double fitness;

	/**
	 * The policy parameter of the classifier
	 */
	public double omega;

	/**
	 * The experience (in timesteps) of this classifier.
	 */
	public int experience;

	/**
	 * The timestamp of the last time the GA was run on a set this classifier
	 * was in
	 */
	public int timestamp;

	/**
	 * The average size of the action set this classifier was in
	 */
	public double averageSize;

	/**
	 * The numerosity of the classifier. This is the number of micro-classifier
	 * this macro-classifier represents.
	 */
	public int numerosity;

	/**
	 * The condition of this classifier, made up a binary string with '#'
	 * wildcards
	 */
	public String condition;

	public String conditionNext;

	/**
	 * The avg R and Q for the Q-vector, R+Q=V=real prediction The N is
	 * represent how many times this action in current state was choosed, which
	 * is the experience before.
	 */
	private Qvector R;
	private ArrayList<Qvector> Q;
	private ArrayList<Qvector> V;
	private ArrayList<MinDistanceV> Vset;
	private IDistanceCalculator disCalc;

	public Qvector getR() {
		return R;
	}

	public void setR(Qvector r) {
		R = r;
	}

	public ArrayList<Qvector> getQ() {
		return Q;
	}

	public void setQ(ArrayList<Qvector> q) {
		Q = q;
	}

	public ArrayList<Qvector> getV() {
		return V;
	}

	public void setInitV(ArrayList<Qvector> v) {
		V = v;
		Vset.add(new MinDistanceV(v));
	}

	public ArrayList<MinDistanceV> getVset() {
		return Vset;
	}

	public int sumExp() {
		int sumExp = 0;
		for (MinDistanceV m : this.Vset) {
			sumExp += m.getExp();
		}
		return sumExp;
	}

	public void setV(ArrayList<Qvector> v, ArrayList<Qvector> P) {
		boolean flag = false;
		// if Vset empty
		if (Vset.size() < 1) {
			flag = false;

		} else {
			// if v match one of Vset
			for (MinDistanceV minDisV : Vset) {
				if (v.equals(minDisV.getNewV())) {
					minDisV.increaseExp();
					flag = true;
				}
				minDisV.setAvgDis(disCalc.getDistance(P, minDisV.getNewV()));
				// System.out.println("VSET P:" + P + " v:" + v + "VSET avgDis:"
				// + minDisV.getAvgDis());
			}
		}
		// if v doenst match any of Vset
		if (flag == false) {
			MinDistanceV temp = new MinDistanceV(v);
			temp.increaseExp();
			temp.setAvgDis(disCalc.getDistance(P, v));
			Vset.add(temp);
		}

		chooseV();
		// trimVset();
	}

	private void chooseV() {
		boolean flag = false;
		ArrayList<MinDistanceV> validVset = new ArrayList<MinDistanceV>();
//		Collections.sort(Vset, (a, b) -> (int) ((a.getAvgDis() - b.getAvgDis()) * 10024));
		Collections.sort(Vset, new Comparator<MinDistanceV>() {
			@Override
			public int compare(MinDistanceV a, MinDistanceV b) {
				return a.getAvgDis() == b.getAvgDis() ? 0 : (a.getAvgDis() > b.getAvgDis() ? 1 : -1);
			}
		});
		
		if (Vset.size() > 1) {
			validVset = (ArrayList<MinDistanceV>) Vset.stream().filter(v -> v.getExp() > 1)
					.collect(Collectors.toList());

			if (validVset.size() == 0) {
				validVset.addAll(Vset);
			}
		} else {
			validVset.addAll(Vset);
		}
		// if (V.size() == 0) {
		// V.addAll(Vset.get(0).getNewV());
		// } else {
		// V = Vset.get(0).getNewV();
		// }
		V.clear();
		try {
			V.addAll(validVset.get(0).getNewV());
		} catch (Exception e) {
			System.out.println(String.format("VVVVVVVVVV"));
		}
	}

	private void trimVset() {
		if (Vset.size() > 3) {
//			Collections.sort(Vset, (a, b) -> (int) ((a.getAvgDis() - b.getAvgDis()) * 10024));
			Collections.sort(Vset, new Comparator<MinDistanceV>() {
				@Override
				public int compare(MinDistanceV a, MinDistanceV b) {
					return a.getAvgDis() == b.getAvgDis() ? 0 : (a.getAvgDis() > b.getAvgDis() ? 1 : -1);
				}
			});
			Vset.remove(Vset.size() - 1);
		}

	}

	/**
	 * Construct a classifier with the default values, building a random
	 * condition
	 * 
	 * @param params
	 *            The parameters to use when building the classifier
	 */
	public Classifier(NXCSParameters params) {
		id = GLOBAL_ID;
		GLOBAL_ID++;

		// Set up the default settings
		action = XienceMath.randomInt(params.numActions);
		prediction = params.initialPrediction;
		error = params.initialError;
		fitness = params.initialFitness;
		// omega = params.initialOmega;
		experience = 0;
		timestamp = 0;
		averageSize = 1;
		numerosity = 1;
		
		IDistanceCalculator disCalc = params.disCalc;

		// this.cloner = new Cloner();

		// Build the condition
		StringBuilder build = new StringBuilder();
		for (int i = 0; i < params.stateLength; i++) {
			if (XienceMath.random() < params.pHash) {
				build.append('#');
			} else if (XienceMath.random() < 0.5) {
				build.append('0');
			} else {
				build.append('1');
			}
		}
		condition = build.toString();

		for (int i = 0; i < params.stateLength; i++) {
			build.append('0');
		}
		conditionNext = build.toString();
		this.disCalc = params.disCalc;
	}

	/**
	 * Constructs a classifier with the default values, building the condition
	 * from the given state (For covering)
	 * 
	 * @param params
	 *            The parameters to use when building the classifier
	 * @param state
	 *            The state that the condition of this classifier should match
	 */
	public Classifier(NXCSParameters params, String state) {
		id = GLOBAL_ID;
		GLOBAL_ID++;

		// Set up the default settings
		action = XienceMath.randomInt(params.numActions);
		prediction = params.initialPrediction;
		error = params.initialError;
		fitness = params.initialFitness;
		omega = params.initialOmega;
		experience = 0;
		timestamp = 0;
		averageSize = 1;
		numerosity = 1;
		R = new Qvector(params.intR);
		Q = params.intQ.stream().map(d -> d.clone()).collect(toCollection(ArrayList::new));
		;
		V = params.intV.stream().map(d -> d.clone()).collect(toCollection(ArrayList::new));
		;
		Vset = new ArrayList<MinDistanceV>();

		// Build from the state
		StringBuilder build = new StringBuilder();
		for (int i = 0; i < params.stateLength; i++) {
			if (XienceMath.random() < params.pHash) {
				build.append('#');
			} else {
				build.append(state.charAt(i));
			}
		}
		condition = build.toString();

		for (int i = 0; i < params.stateLength; i++) {
			build.append('0');
		}
		conditionNext = build.toString();
		this.disCalc = params.disCalc;
	}

	/**
	 * Mutates this classifier based on the given values, reconstructing the
	 * condition based on the given state and possibly changing the action.
	 * 
	 * @see NXCSParameters#mutationRate
	 * @see NXCSParameters#numActions
	 * 
	 * @param state
	 *            The state to mutate with. This mutation ensures that the
	 *            condition of this classifier still matches this state
	 * @param numActions
	 *            The number of actions in the system, so that we can choose a
	 *            new one if necessary
	 */
	void mutate(String state, double mutationRate, int numActions) {
		StringBuilder build = new StringBuilder();
		for (int i = 0; i < state.length(); i++) {
			if (XienceMath.random() < mutationRate) {
				if (condition.charAt(i) == '#') {
					build.append(state.charAt(i));
				} else {
					build.append('#');
				}
			} else {
				build.append(condition.charAt(i));
			}
		}

		condition = build.toString();

		if (XienceMath.random() < mutationRate) {
			action = XienceMath.randomInt(numActions);
		}
	}

	/**
	 * Calculates the vote for this classifier to be deleted
	 * 
	 * @see NXCSParameters#thetaDel
	 * @see NXCSParameters#delta
	 * 
	 * @param averageFitness
	 *            The average fitness in the population of classifiers
	 * @return The vote from this classifier for its deletion
	 */
	double deleteVote(double averageFitness, int thetaDel, double delta) {
		double vote = averageSize * numerosity;
		if (experience > thetaDel && fitness / numerosity < delta * averageFitness) {
			return vote * averageFitness / (fitness / numerosity);
		}
		return vote;
	}

	/**
	 * Returns whether this classifier has the requirements to subsume others
	 * 
	 * @see NXCSParameters#thetaSub
	 * @see NXCSParameters#e0
	 * @return True if this classifier can subsume others, false otherwise
	 */
	boolean couldSubsume(double thetaSub, double e0) {
		return experience > thetaSub && error < e0;
	}

	/**
	 * Returns whether this classifier is more general than the other. That is,
	 * it has more wildcards, and their conditions match.
	 * 
	 * @param other
	 *            The classifier to check this classifier is more general than
	 * @return True if this classifier is more general than the other
	 */
	boolean isMoreGeneral(Classifier other) {
		long selfWildcards = condition.chars().filter(c -> c == '#').count();
		long otherWildcards = other.condition.chars().filter(c -> c == '#').count();

		if (selfWildcards <= otherWildcards) {
			return false;
		}

		return IntStream.range(0, condition.length())
				.allMatch(i -> condition.charAt(i) == '#' || condition.charAt(i) == other.condition.charAt(i));
	}

	/**
	 * Returns whether this classifier can subsume the given one. That is, it
	 * has the ability to subsume, and it is more general than the other.
	 * 
	 * @param other
	 *            The classifier to check that this can subsume
	 * @see NXCSParameters#thetaSub
	 * @see NXCSParameters#e0
	 * @return True if this classifier can subsume the other, false otherwise
	 */
	boolean doesSubsume(Classifier other, int thetaSub, double e0) {
		return action == other.action && couldSubsume(thetaSub, e0) && isMoreGeneral(other);
	}

	/**
	 * Performs a deepclone of this Classifier, returning the new Classifier
	 * 
	 * @return The classifier which is an exact clone of this (barring the ID)
	 */
	Classifier deepcopy() {
		// Basically we serialize this and then deserialize into a new object
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
			final ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			oos.close();

			final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
			Classifier cl = (Classifier) ois.readObject();
			cl.id = GLOBAL_ID;
			GLOBAL_ID++;
			return cl;
		} catch (final Exception e) {
			throw new RuntimeException("Cloning failed");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof Classifier))
			return false;
		Classifier clas = (Classifier) other;

		return clas.id == id;
	}

	// /**
	// * {@inheritDoc}
	// */
	// public String toString(){
	// StringBuilder build = new StringBuilder();
	// build.append(String.format("Classifier [%s = %d, Theta: %3.2f, Error:
	// %3.2f, Prediction: %3.2f, Numerosity: %d]", condition, action, omega,
	// error, prediction, numerosity));
	//
	// return build.toString();
	// }

	public void initiateAfterCopied(NXCSParameters params) {
		this.Vset.clear();
		ArrayList<Qvector> iniV1 = new ArrayList<Qvector>();
		iniV1.add(new Qvector(-10, -10));
		this.setInitV(iniV1);
		this.R = new Qvector(params.intR);
		this.Q = params.intQ.stream().map(d -> d.clone()).collect(toCollection(ArrayList::new));
	}

	public int wildcardCount() {
		int count = 0;
		for (int i = 0; i < condition.length(); i++) {
			if (condition.charAt(i) == '#') {
				count++;
			}

		}

		return count;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(
				"&&&&&Classifier [%s = %d\t Theta: %3.2f\t Error: %3.2f\tFitness: %3.2f\tPrediction: %3.2f\tNumerosity: %d\texp:%d\tCondictionNext:%s",
				condition, action, omega, error, fitness, prediction, numerosity, experience, this.conditionNext));

		sb.append(" R:[");
		for (Double p : this.R.getQvalue()) {
			sb.append(p.toString());
			sb.append(", ");
		}
		sb.append("]");

		sb.append(" Q:");
		for (Qvector q : this.Q) {
			sb.append("[");
			for (Double p : q.getQvalue()) {
				sb.append(p.toString());
				sb.append(", ");
			}
			sb.append("]");
		}

		sb.append(" V:");
		for (Qvector q : this.V) {
			sb.append("[");
			for (Double p : q.getQvalue()) {
				sb.append(p.toString());
				sb.append(", ");
			}
			sb.append("]");
		}

		sb.append(" Vset:[ ");
		for (MinDistanceV q : this.Vset) {
			sb.append(q.toString());
			sb.append("], ");
		}
		sb.append("]");
		sb.append("\n");
		return sb.toString().replace("], ", "]").replace(", ]", "]");
	}
}