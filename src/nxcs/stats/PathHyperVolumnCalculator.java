package nxcs.stats;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import nxcs.HyperVolumn;
import nxcs.Qvector;
import nxcs.addVectorNList;

public class PathHyperVolumnCalculator {
	private HyperVolumn hv;
	private addVectorNList vtr;

	public PathHyperVolumnCalculator(HyperVolumn hv, addVectorNList vtr) {
		this.hv = hv;
		this.vtr = vtr;
	}

	public double calculateHyperVolumnForWeight(ArrayList<StepSnapshot> stats, HashMap<Point, Qvector> rewards) {
		Qvector reward = null;
		ArrayList<Qvector> list = new ArrayList<Qvector>();
		List<StepSnapshot> validStats = stats.stream().filter(x -> x.getSteps() > 0).collect(Collectors.toList());
		for (StepSnapshot stat : validStats) {
			if (rewards.containsKey(stat.getFinalState())) {
				reward = rewards.get(stat.getFinalState());
			} else {
				reward = rewards.get(new Point(-1, -1));
			}
			list.add(this.getPathPayload(stat, reward));
		}


		double sum = 0;
		for (Qvector q : list) {
			sum += hv.calcHyperVolumn(q, new Qvector(-100, -100));
		}
		
		System.out.println("-------------------------Path list ----------------------");
		for (StepSnapshot s : stats) {
			System.out.println(s.toString());
		}
		System.out.println("-------------------------Valid Path list ----------------------");
		for (StepSnapshot s : validStats) {
			System.out.println(s.toString());
		}

		System.out.println("-------------------------Qvector list ----------------------");
		for (Qvector q : list) {
			System.out.println(q.toString());
		}
		System.out.println("-------------------------Qvector list Result:----------------------" + sum);
		System.out.println("");
		return sum;
	}

	public Qvector getPathPayload(StepSnapshot p, Qvector reward) {
		return vtr.addVector(new Qvector(-(p.getSteps() - 1), 0), reward);
	}
}
