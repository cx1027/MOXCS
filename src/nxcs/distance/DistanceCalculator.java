package nxcs.distance;

import java.util.ArrayList;
import java.util.List;

import com.rits.cloning.Cloner;

import nxcs.Qvector;

public abstract class DistanceCalculator implements IDistanceCalculator {
	private ArrayList<Qvector> qSet1;
	private ArrayList<Qvector> qSet2;

	/*
	 * get distance between two qSet
	 */
	public abstract double getDistance(ArrayList<Qvector> qSet1, ArrayList<Qvector> qSet2);

	// public double getCoreDistance(ArrayList<Qvector> qSet1,
	// ArrayList<Qvector> qSet2) {
	// ArrayList<Double> core0 = null;
	// ArrayList<Double> core1 = null;
	// double distance = 0;
	//
	// core0 = calculateCore(qSet1);
	// core1 = calculateCore(qSet2);
	//
	// distance = Math.pow((core0.get(0) - core1.get(0)), 2) +
	// Math.pow((core0.get(1) - core1.get(1)), 2);
	//
	// return distance;
	// }
	//
	// public ArrayList<Double> calculateCore(ArrayList<Qvector> qSet) {
	// ArrayList<Double> core = null;
	// ArrayList<Double> sum = new ArrayList<Double>();
	// for (int i = 0; i < qSet.get(0).size(); i++) {
	// sum.add(0d);
	// }
	//
	// for (Qvector q : qSet) {
	// for (int i = 0; i < q.size(); i++) {
	// sum.set(i, q.getQvalue().get(i) + sum.get(i));
	// }
	// }
	//
	// for (int i = 0; i < qSet.get(0).size(); i++) {
	// sum.set(i, sum.get(i) / qSet.size());
	// }
	//
	// return sum;
	// }

	// public double getMINDistance(ArrayList<Qvector> qSet1, ArrayList<Qvector>
	// qSet2) {
	// double dis = 0;
	// double min = 0;
	// int flag = 0;
	//
	// for (Qvector q1 : qSet1) {
	// for (Qvector q2 : qSet2) {
	// if (flag == 0) {
	// min = getDisofPonit(q1, q2);
	// flag = 1;
	// } else {
	// dis = getDisofPonit(q1, q2);
	// if (dis < min) {
	// min = dis;
	// }
	// }
	// }
	// }
	//
	// return min;
	// }

	public double getDisofPonit(Qvector q1, Qvector q2) {
		double distance = 0;
		distance = Math.pow((q1.get(0) - q2.get(0)), 2) + Math.pow((q1.get(1) - q2.get(1)), 2);
		return distance;
	}

	// public double getMAXDistance(ArrayList<Qvector> qSet1, ArrayList<Qvector>
	// qSet2) {
	// double dis = 0;
	// double max = 0;
	// int flag = 0;
	//
	// for (Qvector q1 : qSet1) {
	// for (Qvector q2 : qSet2) {
	// if (flag == 0) {
	// max = getDisofPonit(q1, q2);
	// flag = 1;
	// } else {
	// dis = getDisofPonit(q1, q2);
	// if (dis > max) {
	// max = dis;
	// }
	// }
	// }
	// }
	//
	// return max;
	// }

	public double getAVGDistance(ArrayList<Qvector> qSet1, ArrayList<Qvector> qSet2) {
		double sum = 0;
		double num = 0;

		for (Qvector q1 : qSet1) {
			for (Qvector q2 : qSet2) {
				sum += getDisofPonit(q1, q2);
			}
		}

		num = qSet1.size() * qSet2.size();
		return sum / num;
	}

	// public double getJDistance(ArrayList<Qvector> qSet1, ArrayList<Qvector>
	// qSet2) {
	//
	// int uniSize = getUnionSet(qSet1, qSet2).size();
	// int interSize = getIntersectionSet(qSet1, qSet2).size();
	//
	// return Math.abs(uniSize-interSize)/(double)uniSize;
	// }

	public ArrayList<Qvector> getIntersectionSet(ArrayList<Qvector> qSet1, ArrayList<Qvector> qSet2) {
		ArrayList<Qvector> intersection = new ArrayList<Qvector>();
		for (Qvector q : qSet1) {
			if (qSet2.contains(q)) {
				intersection.add(q);
			}
		}
		return intersection;
	}

	public ArrayList<Qvector> getUnionSet(ArrayList<Qvector> qSet1, ArrayList<Qvector> qSet2) {
		ArrayList<Qvector> union = new ArrayList<Qvector>(qSet1);
		for (Qvector q : qSet2) {
			if (!union.contains(q)) {
				union.add(q);
			}
		}
		return union;
	}

	public double get10Distance(ArrayList<Qvector> qSet1, ArrayList<Qvector> qSet2) {
		double dis = 0;
		int flag = 0;

		if (qSet1.size() == qSet2.size()) {
			for (Qvector q1 : qSet1) {
				for (Qvector q2 : qSet2) {
					if (q1.equals(q2)) {
						flag += 1;
					}
				}
			}
		}

		if (flag >= 1 && flag == qSet1.size()) {
			dis = 0;
		} else {
			dis = 1;
		}

		return dis;
	}

}
