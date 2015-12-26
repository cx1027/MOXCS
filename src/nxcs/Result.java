package nxcs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Result {
	private int finailStateCount;
	private List<PathStep> pathsteps;

	public int getFinailStateCount() {
		return finailStateCount;
	}

	public void setFinailStateCount(int finailStateCount) {
		this.finailStateCount = finailStateCount;
	}
	// public List<PathStep> getPathsteps() {
	// return pathsteps;
	// }
	// public void setPathsteps(List<PathStep> pathsteps) {
	// this.pathsteps = pathsteps;
	// }

	public void addPathStep(PathStep pathStep) {
		this.pathsteps.add(pathStep);
	}

	

	public int getAllSteps() {
		int allsteps = 0;
		for (PathStep ps : pathsteps) {
			allsteps += ps.getStep();
		}
		return allsteps;
	}

	public double getAvgSteps() {
		return getAllSteps() / finailStateCount;
	}

	public Result() {
		this(0, new ArrayList<PathStep>());
	}

	public Result(int finailStateCount, ArrayList<PathStep> pathsteps) {
		this.finailStateCount = finailStateCount;
		this.pathsteps = pathsteps;
	}

	
	@Override
	public String toString() {
		return "Result [finailStateCount=" + finailStateCount  + ", getAllSteps()="
				+ getAllSteps() + ", getAvgSteps()=" + getAvgSteps() + "]";
	}
}
