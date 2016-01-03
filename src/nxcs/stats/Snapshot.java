package nxcs.stats;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Condition;

import nxcs.Classifier;

public class Snapshot {
	private final int populationSize;
	private final double macroClassifierProportion;
	private final double averageFitness;
	private final double averageSpecificity;
	private final int time;
	private final double performance;
	private final double hypervolumn;

	public Snapshot(int timestamp, List<Classifier> population, double perf, int length, double hyper) {
		time = timestamp;
		populationSize = population.size();
		macroClassifierProportion = calculateMacroPopulationProportion(population);
		averageFitness = calculateAverageFitness(population);
		averageSpecificity = calculateAverageSpecificity(population, length);
		performance = perf;
		hypervolumn = hyper;
	}

	private Snapshot(int popSize, double avFitness, int stamp, double hyper) {
		populationSize = popSize;
		macroClassifierProportion = 0;// macroClassifierProp;
		averageFitness = avFitness;
		averageSpecificity = 0;// avSpec;
		time = stamp;
		hypervolumn = hyper;
		performance = 0;// perf;
	}

	private double calculateMacroPopulationProportion(List<Classifier> population) {
		int numSum = 0;
		for (Classifier classifier : population) {
			if (classifier.numerosity > 1) {
				numSum++;
			}
		}

		if (numSum == 0)
			return 0;

		return numSum / (double) population.size();
	}

	private double calculateAverageFitness(List<Classifier> population) {
		double fitnessSum = 0;
		int numSum = 0;
		for (Classifier classifier : population) {
			fitnessSum += classifier.fitness * classifier.numerosity;
			numSum += classifier.numerosity;
		}

		if (numSum == 0)
			return 0;

		return fitnessSum / numSum;
	}

	private double calculateAverageSpecificity(List<Classifier> population, int length) {
		int specificitySum = 0;
		for (Classifier classifier : population) {
			specificitySum += (length - classifier.wildcardCount());
		}

		return specificitySum / (double) population.size();
	}

	public int getTimestamp() {
		return time;
	}

	public double getPopulationSize() {
		return populationSize;
	}

	public double getAverageFitness() {
		return averageFitness;
	}

	public double getAverageSpecificity() {
		return averageSpecificity;
	}

	public double getMacroClassifierProportion() {
		return macroClassifierProportion;
	}

	public double getPerformance() {
		return performance;
	}

	public double getHypervolumn() {
		return this.hypervolumn;
	}

	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		build.append(String.format("Snapshot at %d timesteps%n", time));
		build.append(String.format("Population Size: %d%n", populationSize));
		build.append(String.format("Average Fitness across Classifiers: %3.2f%n", averageFitness));
		build.append(String.format("Average Specificity: %3.2f%n", averageSpecificity));
		build.append(String.format("Macro Classifier Proportion: %3.2f%n", macroClassifierProportion));
		build.append(String.format("Performance: %3.2f%n", performance));
		build.append(String.format("HyperVolumn: %3.2f%n", this.hypervolumn));

		return build.toString();
	}

	public String toCSV() {
		StringBuilder build = new StringBuilder();
		build.append(time);
		build.append(", ");
		build.append(populationSize);
		build.append(", ");
		build.append(averageFitness);
		build.append(", ");
		build.append(averageSpecificity);
		build.append(", ");
		build.append(macroClassifierProportion);
		build.append(", ");
		build.append(performance);
		build.append(", ");
		build.append(this.hypervolumn);
		build.append("\n");

		return build.toString();
	}

	/*
	 * calculate average values of trails
	 */
	public static Snapshot average(List<Snapshot> snapshots) {
		// Snapshot(int popSize, double avFitness, int stamp, double hyper)
		if (snapshots.size() == 0) {
			return new Snapshot(0, 0, 0, 0);
		}
		double avPopSize = 0;
		double avFitness = 0;
		double avSpec = 0;
		double avMacroProp = 0;
		double perf = 0;
		double hyperv = 0;

		for (Snapshot snapshot : snapshots) {
			avPopSize += snapshot.populationSize;
			avFitness += snapshot.averageFitness;
			avSpec += snapshot.averageSpecificity;
			avMacroProp += snapshot.macroClassifierProportion;
			perf += snapshot.performance;
			hyperv += snapshot.hypervolumn;
		}

		int size = snapshots.size();
		// return new Snapshot((int)(avPopSize / size), perf / size, avMacroProp
		// / size, avFitness / size, avSpec / size, snapshots.get(0).time);
		return new Snapshot((int) (avPopSize / size), avFitness / size, snapshots.get(0).time, hyperv / size);
	}
}
