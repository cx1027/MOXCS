package nxcs.distance;

import java.util.ArrayList;

import nxcs.Qvector;

public class CoreDistanceCalculator extends DistanceCalculator {

	@Override
	public double getDistance(ArrayList<Qvector> qSet1, ArrayList<Qvector> qSet2) {
		ArrayList<Double> core0 = null;
		ArrayList<Double> core1 = null;
		double distance = 0;

		core0 = calculateCore(qSet1);
		core1 = calculateCore(qSet2);

		distance = Math.pow((core0.get(0) - core1.get(0)), 2) + Math.pow((core0.get(1) - core1.get(1)), 2);

		return distance;
	}


	private ArrayList<Double> calculateCore(ArrayList<Qvector> qSet) {
		ArrayList<Double> core = null;
		ArrayList<Double> sum = new ArrayList<Double>();
		for (int i = 0; i < qSet.get(0).size(); i++) {
			sum.add(0d);
		}

		for (Qvector q : qSet) {
			for (int i = 0; i < q.size(); i++) {
				sum.set(i, q.getQvalue().get(i) + sum.get(i));
			}
		}

		for (int i = 0; i < qSet.get(0).size(); i++) {
			sum.set(i, sum.get(i) / qSet.size());
		}

		return sum;
	}
}
